mvn clean package -Dmaven.test.skip=true;
java -jar -Dspring.profiles.active=dev ./target/weatherApp*.jar "run.date(date)=$CURRENT_DATE";
read;
