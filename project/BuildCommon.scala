import sbt._

object BuildCommon {
	def version = "1.0-SNAPSHOT"
	def scalaVersion = "2.11.7"
	def scalacOption= Seq("-language:higherKinds",
            "-encoding", "UTF-8",
            "-Xlint",
            "-deprecation",
            "-Xfatal-warnings",
            "-feature",
            "-language:postfixOps",
            "-unchecked",
            "-language:implicitConversions", 
            "-target:jvm-1.6")
	def javacOptions =  Seq("-source", "1.6", "-target", "1.6")

	def resolvers = Seq(
        "cloudera.cld" at "http://repository.cloudera.com/artifactory/public/",
        "ebaycentral_central" at "http://ebaycentral.qa.ebay.com/content/repositories/central/",
        "sbe.nexus.releases" at "http://repository.qa.ebay.com/nexus/ebay.sbe.releases",
        "ebay-central-releases" at "http://ebaycentral.qa.ebay.com/content/repositories/releases/",
        "ebay-central-snapshots" at "http://ebaycentral.qa.ebay.com/content/repositories/snapshots/",
        "ebay-central-thirdparty" at "http://ebaycentral.qa.ebay.com/content/repositories/thirdparty",
        "sbe.nexus.3rdparty" at "http://nxrepository.corp.ebay.com/nexus/content/repositories/ebay.sbe.3rdparty",
        "hortonworks" at "http://nxrepository.corp.ebay.com/nexus/content/repositories/hortonworks.repo",
        "sbe.nexus.snapshots" at "http://nxrepository.corp.ebay.com/nexus/content/repositories/ebay.sbe.snapshots/",
        "sbe.nexus.releases1" at "http://nxrepository.corp.ebay.com/nexus/content/repositories/ebay.sbe.releases",
        "maven central" at "http://nxrepository.qa.ebay.com/nexus/content/repositories/central",
        "cloudera.releases" at "http://nxrepository.corp.ebay.com/nexus/content/repositories/cloudera.releases/",
        "sbe.3rdParty" at "http://nxrepository.corp.ebay.com/nexus/content/repositories/ebay.sbe.3rdparty/",
        "maven.central.cloud" at "http://nxrepository.qa.ebay.com/nexus/maven.central.cloud")

}
