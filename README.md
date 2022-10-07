# sc-library

## Modpacks

Modpack use: **allowed**

This mod (`sc-library`) was created primarily for use by the SwitchCraft server, but you are **allowed** to use the
mod in your own modpack.

Please note that each custom SwitchCraft mod has its own license, so check the license of each mod before using it in
your modpack.

## Usage

![](https://repo.lem.sh/api/badge/latest/releases/pw/switchcraft/sc-library?name=Latest%20version)
```properties
# gradle.properties
scLibraryVersion = <version>
```

```kotlin
// build.gradle.kts
val scLibraryVersion: String by project

repositories {
  maven {
    url = uri("https://repo.lem.sh/releases")
    content {
      includeGroup("pw.switchcraft")
    }
  }
}

dependencies {
  modImplementation(include("pw.switchcraft", "sc-library", scLibraryVersion))
}
```

## License

This repository is licensed under the [MIT license](LICENSE).
