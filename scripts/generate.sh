script_dir=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
mvn compile -f $script_dir/../pom.xml
mvn package -f $script_dir/../pom.xml
mkdir -p ../rendu
mv $script_dir/../target/cringebot.jar $script_dir/../rendu