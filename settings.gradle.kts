rootProject.name = "fsma-server"

dependencyResolutionManagement {

    versionCatalogs {
        create("libs") {
            files("./gradle/libs.versions.toml")
        }
    }
}
