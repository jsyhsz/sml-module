package org.hw.sml.redis;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import org.hw.sml.support.cache.CacheManager;
import org.hw.sml.tools.MapUtils;
import org.hw.sml.tools.SerializationUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisPoolCacheManager implements CacheManager{
	private String url;
	
	private JedisPool jedisPool;
	
	private RedisTemplate redisTemplate;
	
	public void init() {
		if(url!=null&&jedisPool==null){
			jedisPool=JedisPoolFactory.create(url);
		}
		redisTemplate=new RedisTemplate(jedisPool);
	}
	@Override
	public void destroy() {
		jedisPool.destroy();
	}
	@Override
	public Object get(final String key) {
		return redisTemplate.execute(new RedisCallback<Object>() {
			public Object doJedisCallback(Jedis jedis) {
				byte[] bytes=jedis.get(toByte(key));
				if(bytes==null)
					return null;
				return SerializationUtils.deserialize(bytes);
			}
		});
	}

	@Override
	public void set(final String key,final Object value,final int minutes) {
		if(value!=null)
		redisTemplate.execute(new RedisCallback<Object>() {
			public Object doJedisCallback(Jedis jedis) {
				jedis.set(toByte(key),SerializationUtils.serialize((Serializable)value));
				if(minutes>0)
				jedis.expire(toByte(key), minutes*60);
				return null;
			}
		});
	}

	@Override
	public boolean contain(final String key) {
		return redisTemplate.execute(new RedisCallback<Boolean>() {
			public Boolean doJedisCallback(Jedis jedis) {
				return jedis.exists(toByte(key));
			}
		});
	}

	@Override
	public void remove(final String key) {
	   redisTemplate.execute(new RedisCallback<Object>() {
			public Object doJedisCallback(Jedis jedis) {
				return jedis.del(toByte(key));
			}
		});
	}

	@Override
	public int clearKeyStart(final String keyStart) {
		if(keyStart==null||keyStart.trim().length()==0)
			return 0;
		return redisTemplate.execute(new RedisCallback<Integer>() {
			public Integer doJedisCallback(Jedis jedis) {
				Set<String> keys=jedis.keys(keyStart+"*");
				long size=jedis.del(keys.toArray(new String[]{}));
				return (int)size;
			}
		});
	}

	@Override
	public Map<String,Object> getKeyStart(final String keyStart) {
		return redisTemplate.execute(new RedisCallback<Map<String, Object>>() {
			public Map<String, Object> doJedisCallback(Jedis jedis) {
				Map<String,Object> result=MapUtils.newLinkedHashMap();
				Set<String> keys=jedis.keys(keyStart+"*");
				for(String key:keys){
					result.put(key,null);
				}
				return result;
			}
		});
	}
	@Override
	public int clear() {
		return 0;
	}
	public byte[] toByte(String name){
		return name.getBytes(Charset.forName("utf8"));
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
}
