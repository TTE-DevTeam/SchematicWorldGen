package de.dertoaster.schematicworldgen;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

public final class TTEWorldGenClassLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        // WorldEdit API
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("Enginehub", "default", "https://maven.enginehub.org/repo/").build());
        resolver.addDependency(new Dependency(new DefaultArtifact("com.sk89q.worldedit:worldedit-core:7.3.0"), null));
        classpathBuilder.addLibrary(resolver);

        // FastUtil
        resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("mvnrepository", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());
        resolver.addDependency(new Dependency(new DefaultArtifact("it.unimi.dsi:fastutil:8.5.11"), null));
        classpathBuilder.addLibrary(resolver);

        // Caffeine
        resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("mvnrepository", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());
        resolver.addDependency(new Dependency(new DefaultArtifact("com.github.ben-manes.caffeine:caffeine:3.2.4"), null));
        classpathBuilder.addLibrary(resolver);
    }

}
