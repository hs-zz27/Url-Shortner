local key = KEYS[1]
local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local limit = tonumber(ARGV[3])
local member = ARGV[4]

-- 1. drop entries older than the window
redis.call('ZREMRANGEBYSCORE', key, 0, now - window)

-- 2. count requests currently in the window
local count = redis.call('ZCARD', key)

-- 3. allow or reject
if count < limit then
    redis.call('ZADD', key, now, member)
    redis.call('PEXPIRE', key, window)
    return 1        -- allowed
else
    return 0        -- rejected
end