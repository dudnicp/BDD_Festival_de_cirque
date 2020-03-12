init:
	javac -d bin -classpath bin:jars/ojdbc6.jar -sourcepath src src/InitBdd.java
	java -classpath bin:jars/ojdbc6.jar InitBdd

runApp:
	javac -d bin -classpath bin:jars/ojdbc6.jar -sourcepath src src/Exec.java
	java -classpath bin:jars/ojdbc6.jar Exec
