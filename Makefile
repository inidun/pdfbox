
#CLASSPATH="$(curpath)/examples/target/classes:$(m2path)/repository/org/bouncycastle/bcmail-jdk15on/1.68/bcmail-jdk15on-1.68.jar:$(m2path)/repository/org/bouncycastle/bcprov-jdk15on/1.68/bcprov-jdk15on-1.68.jar:$(m2path)/repository/org/bouncycastle/bcpkix-jdk15on/1.68/bcpkix-jdk15on-1.68.jar:$(curpath)/tools/target/classes:$(curpath)/debugger/target/classes:$(m2path)/repository/info/picocli/picocli/4.6.1/picocli-4.6.1.jar:$(curpath)/xmpbox/target/classes:$(m2path)/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:$(curpath)/pdfbox/target/classes:$(curpath)/fontbox/target/classes:$(m2path)/repository/org/apache/lucene/lucene-core/7.7.2/lucene-core-7.7.2.jar:$(m2path)/repository/org/apache/lucene/lucene-analyzers-common/7.7.2/lucene-analyzers-common-7.7.2.jar:$(m2path)/repository/org/apache/ant/ant/1.10.10/ant-1.10.10.jar:$(m2path)/repository/org/apache/ant/ant-launcher/1.10.10/ant-launcher-1.10.10.jar"


.PHONY: execute

curpath="$(shell pwd)"
m2path="$(HOME)/.m2"
classpath="$(curpath)/examples/target/classes:$(m2path)/repository/org/bouncycastle/bcmail-jdk15on/1.68/bcmail-jdk15on-1.68.jar:$(m2path)/repository/org/bouncycastle/bcprov-jdk15on/1.68/bcprov-jdk15on-1.68.jar:$(m2path)/repository/org/bouncycastle/bcpkix-jdk15on/1.68/bcpkix-jdk15on-1.68.jar:$(curpath)/tools/target/classes:$(curpath)/debugger/target/classes:$(m2path)/repository/info/picocli/picocli/4.6.1/picocli-4.6.1.jar:$(curpath)/xmpbox/target/classes:$(m2path)/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:$(curpath)/pdfbox/target/classes:$(curpath)/fontbox/target/classes:$(m2path)/repository/org/apache/lucene/lucene-core/7.7.2/lucene-core-7.7.2.jar:$(m2path)/repository/org/apache/lucene/lucene-analyzers-common/7.7.2/lucene-analyzers-common-7.7.2.jar:$(m2path)/repository/org/apache/ant/ant/1.10.10/ant-1.10.10.jar:$(m2path)/repository/org/apache/ant/ant-launcher/1.10.10/ant-launcher-1.10.10.jar"

execute:
	@java -Dfile.encoding=UTF-8 -classpath "$(classpath)" org.apache.pdfbox.examples.pdmodel.DisturbingThisIs

geh:
	echo "$(HOME)/.m2"
