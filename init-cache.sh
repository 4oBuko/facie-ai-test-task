#!/bin/sh
echo "Waiting for Redis to start..."

# Wait until Redis is available
until redis-cli ping > /dev/null 2>&1; do
    sleep 2
done

echo "Redis is ready. Initializing with CSV data..."

batch_size=1000  # Adjust batch size if needed
batch_file="/tmp/redis_batch.txt"
count=0

# Ensure batch file is empty before starting
> "$batch_file"

tail -n +2 /data/products.csv | while IFS=, read -r key value; do
    echo "SET \"$key\" \"$value\"" >> "$batch_file"
    count=$((count + 1))

    if [ "$count" -ge "$batch_size" ]; then
        redis-cli --pipe < "$batch_file"
        > "$batch_file"  # Clear batch file for next chunk
        count=0
    fi
done

# Send any remaining data
if [ "$count" -gt 0 ]; then
    redis-cli --pipe < "$batch_file"
fi

echo "Redis initialization completed."
