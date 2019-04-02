 mvn deploy:deploy-file \
 -DpomFile=./core/build/poms/pom-default.xml \
 -Dfile=./core/build/libs/video-recorder-core-1.8.3-capsilon.jar \
 -DrepositoryId=capsilon-mposs-repository \
 -Durl=https://mvi-maven-repo.capsilondev.net/artifactory/mposs-release

mvn deploy:deploy-file \
-DpomFile=./junit5/build/poms/pom-default.xml \
-Dfile=./junit5/build/libs/video-recorder-junit5-1.8.3-capsilon.jar \
-DrepositoryId=capsilon-mposs-repository \
-Durl=https://mvi-maven-repo.capsilondev.net/artifactory/mposs-release


