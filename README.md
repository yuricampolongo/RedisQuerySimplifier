
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

```java
@RedisObject(name = "EntityTest")
public class EntityTest {

}
```

**INDEX FIELDS**

You can index an object field to make fast searchs, to do that, just annotate your field with @RedisFieldIndex, obs: you MUST create a public getter for that field

```java
@RedisObject(name = "EntityTest")
public class EntityTest {
    private Long	id;
    @RedisFieldIndex
    private String	name;
	
    public String getName() {
	    return name;
    }
}
```

**SAVE OR UPDATE**

Saves or update the object in Redis
```java
 Long id = 10l;
 EntityTest et = new EntityTest(id, "EntityTest");
 RedisQuery.save(et, id);
```

**FIND FIRST OCCURRENCE**

Find the first occurrence of an object stored in Redis.

```java
Optional<EntityTest> findFirst = RedisQuery.findFirst(EntityTest.class);
```

**FIND BY ID**

Find the occurrence of an object stored in Redis based on ID.

```java
Optional<EntityTest> findById = RedisQuery.findById(EntityTest.class, 10l);
```

**CHECK EXISTS**

Find if the occurrence of an object stored in Redis exists based on search params.

```java
MatchOperatorCombiner where = new MatchOperatorCombiner()
				.where(new MatchOperator("name", "EntityTest", Operator.EQUAL));
				
boolean exists = RedisQuery.exists(EntityTest.class, params);
```

**REMOVE**

Remove the entity and all its indexes

```java
Long id = 11l;
EntityTest et = new EntityTest(id, "EntityTestToRemove");
RedisQuery.remove(et, id);
```

**FIND FIRST FILTERING BY PARAMETERS**

Find the first occurrence of an entity based on indexed parameters

```java
MatchOperatorCombiner where = new MatchOperatorCombiner()
				.where(new MatchOperator("name", "EntityTest", Operator.EQUAL));

RedisQuery.findFirstByParams(EntityTest.class, params);
```

**FIND ALL**

Find all the occurrences of an entity 

```java
List<EntityTest> all = RedisQuery.findAll(EntityTest.class);
```

*This project is in development phase, feel free to add more methods or suggestions, I will update the documentation as long as other methods are created*

**FIND ALL FILTERING BY PARAMETERS**

Find all occurrences of an entity based on indexed parameters

```java
MatchOperatorCombiner where = new MatchOperatorCombiner()
				.where(new MatchOperator("name", "EntityTest", Operator.EQUAL));

RedisQuery.findAllByParams(EntityTest.class, params);
```

**COMPLEX FILTERS**

There is a way to make 'AND', 'OR' filters, 'CONTAINS', 'STARTS_WITH' and 'ENDS_WITH' queries.

The MatchOperatorCombiner class, can be send to all methods that performs search based on indexed fields of an RedisObject annotated class.

The code bellow shows how to make an 'AND' condition and 'EQUAL' filter.

```java
// You can send this object as a parameter to methods findAllByParams, findFirstByParams and exists
MatchOperatorCombiner where = new MatchOperatorCombiner()
				.where(new MatchOperator("name", "EntityToSearch", Operator.EQUAL))
				.and(new MatchOperator("docNumber",123426l,Operator.EQUAL));
```

The code bellow shows how to make an 'OR' condition and 'EQUAL' filter.

```java
// You can send this object as a parameter to methods findAllByParams, findFirstByParams and exists
MatchOperatorCombiner where = new MatchOperatorCombiner()
				.where(new MatchOperator("name", "EntityToSearch", Operator.EQUAL))
				.or(new MatchOperator("docNumber",999999l,Operator.EQUAL));
```

The code bellow shows how to make an 'AND' condition and 'CONTAINS' filter.

```java
// You can send this object as a parameter to methods findAllByParams, findFirstByParams and exists
MatchOperatorCombiner where = new MatchOperatorCombiner()
				.where(new MatchOperator("name", "ToSea", Operator.CONTAINS))
				.and(new MatchOperator("docNumber",342l,Operator.CONTAINS));
```

The code bellow shows how to make an 'OR' condition and 'STARTS_WITH' filter.

```java
// You can send this object as a parameter to methods findAllByParams, findFirstByParams and exists
MatchOperatorCombiner where = new MatchOperatorCombiner()
				.where(new MatchOperator("name", "Entity", Operator.STARTS_WITH))
				.and(new MatchOperator("docNumber",123l,Operator.STARTS_WITH));
```



## Authors

 - Yuri Martins Campolongo

## References
Check the [geralDAO](https://github.com/viictorh/geralDAO) project to create an abstraction layer to your relational database.