mvn clean package -Dmaven.test.skip=true;
java -jar ./target/weatherApp*.jar "run.date(date)=$CURRENT_DATE";
read;
