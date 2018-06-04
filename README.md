# RedisQuerySimplifier
Simplify the use of Redis for simple projects. This project can be used to make simple operations in Redis, like: save, update, select by id, find first occurrence, delete, etc.

This project was created to make your code looks simpler and easy to maintain.



## Build
This project uses Maven to build and tests.
To build, just run the command `mvn clean install -Dtest=Tester test`

## Installation
Add the dependency in your pom.xml:

    <dependency>
		<groupId>br.com.redis.clients</groupId>
		<artifactId>redisquerysimplifier</artifactId>
		<version>${version}</version>
	</dependency>

or add the jar generated in the topic 'BUILD' on your classpath;

## Utilization

RedisQuery is a static class. You must first call the `init` method, passing your server and port to connect to Redis.

   	private static String	server	= "localhost";
	private static Integer	port	= 6379;
    RedisQuery.init(server, port);
    
After that, you can call the methods available:

**REDIS OBJECTS**

All the objects you will use, you need to annotate with @RedisObject annotation

    @RedisObject(name = "EntityTest")
    public class EntityTest {
    
    }

**SAVE OR UPDATE**

Saves or update the object in Redis
     
     Long id = 10l;
     EntityTest et = new EntityTest(id, "EntityTest");
     RedisQuery.save(et, id);
     
**FIND FIRST OCCURRENCE**

Find the first occurrence of an object stored in Redis.

    Optional<EntityTest> findFirst = RedisQuery.findFirst(EntityTest.class);
    
**FIND BY ID**

Find the occurrence of an object stored in Redis based on ID.

    Optional<EntityTest> findById = RedisQuery.findById(EntityTest.class, 10l);
    
*This project is in development phase, feel free to add more methods or suggestions, I will update the documentation as long as other methods are created*

## Authors
 - Yuri Martins Campolongo

## References
Check the [geralDAO](https://github.com/viictorh/geralDAO) project to create an abstraction layer to your relational database.