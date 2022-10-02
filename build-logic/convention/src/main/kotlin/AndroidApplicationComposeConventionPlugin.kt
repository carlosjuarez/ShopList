import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.juvcarl.shoplist.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType


class AndroidApplicationComposeConventionPlugin : Plugin<Project>{
    override fun apply(target: Project) {
        with(target){
            pluginManager.apply("com.android.application")
            val extension = extensions.getByType<BaseAppModuleExtension>()
            configureAndroidCompose(extension)

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {

                add("implementation", libs.findLibrary("coil.kt").get())
                add("implementation", libs.findLibrary("coil.kt.compose").get())

                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())

                add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())

            }

        }
    }
}