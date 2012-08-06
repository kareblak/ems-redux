// IDEA plugin
resolvers += "OSS Sonatype SNAPSHOTS" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers <++= sbtVersion(sv => sv match {
 case v if (v.startsWith("0.11")) => Seq(Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns))
 case _ => Nil
})

libraryDependencies <+= sbtVersion(v => v match {
    case "0.11.2" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11"
    case "0.11.3" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.3-0.2.11.1"
    case x if (x.startsWith("0.12")) => "com.github.siasia" %% "xsbt-web-plugin" % "0.12.0-0.2.11.1"
})

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0-SNAPSHOT")

addSbtPlugin("no.arktekk.sbt" % "aether-deploy" % "0.5")

addSbtPlugin("com.jsuereth" % "xsbt-gpg-plugin" % "0.6")

//resolvers += Classpaths.typesafeResolver

//addSbtPlugin("com.typesafe.startscript" % "xsbt-start-script-plugin" % "0.5.2")
