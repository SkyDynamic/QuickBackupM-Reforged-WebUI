import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
  java
  id("com.github.node-gradle.node")
}

node {
    download = false
    version = "22.12.0"
}

val cleanTask = tasks.register("cleanBuild") {
    group = "build"
    description = "Cleans the web build directory"
    doLast {
        delete("dist")
    }
}

val buildTask = tasks.register<PnpmTask>("buildWeb") {
    args.set(listOf("build"))
    dependsOn(tasks.pnpmInstall)
    dependsOn(cleanTask)
    inputs.dir(project.fileTree("src").exclude("**/*.spec.ts"))
    inputs.dir("node_modules")
    inputs.files("eslint.config.ts", ".npmrc", "tsconfig.json", "uno.config.ts", "vite.config.ts")
    outputs.dir("dist")
}
