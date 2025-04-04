// sbt 1.1.x
addSbtPlugin("com.simplytyped" % "sbt-antlr4" % "0.8.3")

resolvers +=
  Resolver.url(
    "bintray-plugins",
    url("https://dl.bintray.com/resisttheurge/sbt-plugins/")
  )(Resolver.ivyStylePatterns)