package com.cramsan.framework.webroute.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.Modifier
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertTrue

class WebDestinationProcessorTest {
    private fun nameMock(value: String): KSName = mockk { every { asString() } returns value }

    private fun webDestinationSuperType(): KSTypeReference {
        val webDestination =
            mockk<KSClassDeclaration> {
                every { this@mockk.qualifiedName } returns nameMock(WEB_DESTINATION_FQN)
                every { this@mockk.superTypes } returns emptySequence()
            }
        val type = mockk<KSType> { every { declaration } returns webDestination }
        return mockk { every { resolve() } returns type }
    }

    private fun webPathAnnotation(path: String): KSAnnotation {
        val pathArg =
            mockk<KSValueArgument> {
                every { name } returns nameMock("path")
                every { value } returns path
            }
        val resolvedAnnotationType =
            mockk<KSType> {
                every { declaration } returns
                    mockk<KSClassDeclaration> {
                        every { qualifiedName } returns nameMock(WEB_PATH_FQN)
                    }
            }
        return mockk<KSAnnotation> {
            every { annotationType } returns
                mockk<KSTypeReference> { every { resolve() } returns resolvedAnnotationType }
            every { arguments } returns listOf(pathArg)
        }
    }

    private fun subclass(name: String, webPath: String?): KSClassDeclaration =
        mockk {
            every { this@mockk.simpleName } returns nameMock(name)
            every { this@mockk.annotations } returns
                if (webPath != null) sequenceOf(webPathAnnotation(webPath)) else emptySequence()
        }

    private fun sealedRoot(
        packageName: String,
        name: String,
        subclasses: List<KSClassDeclaration>,
    ): KSClassDeclaration =
        mockk {
            every { this@mockk.modifiers } returns setOf(Modifier.SEALED)
            every { this@mockk.qualifiedName } returns nameMock("$packageName.$name")
            every { this@mockk.simpleName } returns nameMock(name)
            every { this@mockk.packageName } returns nameMock(packageName)
            every { this@mockk.superTypes } returns sequenceOf(webDestinationSuperType())
            every { this@mockk.getSealedSubclasses() } returns subclasses.asSequence()
            every { this@mockk.containingFile } returns mockk<KSFile>()
        }

    private fun resolverFor(vararg roots: KSClassDeclaration): Resolver {
        val file = mockk<KSFile> { every { declarations } returns roots.asSequence() }
        return mockk { every { getAllFiles() } returns sequenceOf(file) }
    }

    @Test
    fun `generates a routing object when every subclass has WebPath`() {
        val foo = subclass("FooDestination", "/foo")
        val bar = subclass("BarDestination", "/bar")
        val root = sealedRoot("com.example", "MainDestination", listOf(foo, bar))
        val resolver = resolverFor(root)

        val output = ByteArrayOutputStream()
        val codeGenerator =
            mockk<CodeGenerator> {
                every { createNewFile(any(), "com.example", "MainDestinationWebRoutes", "kt") } returns output
            }
        val logger = mockk<KSPLogger>(relaxed = true)

        WebDestinationProcessor(codeGenerator, logger).process(resolver)

        val generated = output.toByteArray().decodeToString()
        assertTrue(generated.contains("internal object MainDestinationWebRoutes {"))
        assertTrue(generated.contains("webRouteEntry<MainDestination.FooDestination>(\"/foo\")"))
        assertTrue(generated.contains("webRouteEntry<MainDestination.BarDestination>(\"/bar\")"))
        assertTrue(generated.contains("is MainDestination.FooDestination -> fooEntry.route.toWebPath(destination)"))
        verify(exactly = 0) { logger.error(any(), any()) }
    }

    @Test
    fun `reports an error and skips generation when a subclass is missing WebPath`() {
        val foo = subclass("FooDestination", "/foo")
        val bar = subclass("BarDestination", null)
        val root = sealedRoot("com.example", "MainDestination", listOf(foo, bar))
        val resolver = resolverFor(root)

        val codeGenerator = mockk<CodeGenerator>(relaxed = true)
        val logger = mockk<KSPLogger>(relaxed = true)

        WebDestinationProcessor(codeGenerator, logger).process(resolver)

        verify {
            logger.error(
                "MainDestination.BarDestination implements WebDestination but is missing @WebPath(\"/...\")",
                bar,
            )
        }
        verify(exactly = 0) { codeGenerator.createNewFile(any(), any(), any(), any()) }
    }

    @Test
    fun `also generates an aggregator file when aggregator options are provided`() {
        val foo = subclass("FooDestination", "/foo")
        val root = sealedRoot("com.example", "MainDestination", listOf(foo))
        val resolver = resolverFor(root)

        val routesOutput = ByteArrayOutputStream()
        val aggregatorOutput = ByteArrayOutputStream()
        val codeGenerator =
            mockk<CodeGenerator> {
                every { createNewFile(any(), "com.example", "MainDestinationWebRoutes", "kt") } returns routesOutput
                every { createNewFile(any(), "com.example.nav", "AppPathNavigation", "kt") } returns aggregatorOutput
            }
        val logger = mockk<KSPLogger>(relaxed = true)

        WebDestinationProcessor(codeGenerator, logger, "com.example.nav", "AppPathNavigation").process(resolver)

        val generated = aggregatorOutput.toByteArray().decodeToString()
        assertTrue(generated.contains("internal object AppPathNavigation {"))
        assertTrue(generated.contains("MainDestination.fromWebPath(path)"))
    }

    @Test
    fun `does not generate an aggregator file when aggregator options are absent`() {
        val foo = subclass("FooDestination", "/foo")
        val root = sealedRoot("com.example", "MainDestination", listOf(foo))
        val resolver = resolverFor(root)

        val codeGenerator =
            mockk<CodeGenerator> {
                every { createNewFile(any(), any(), any(), any()) } returns ByteArrayOutputStream()
            }
        val logger = mockk<KSPLogger>(relaxed = true)

        WebDestinationProcessor(codeGenerator, logger).process(resolver)

        verify(exactly = 1) { codeGenerator.createNewFile(any(), any(), any(), any()) }
    }

    @Test
    fun `does nothing on a second invocation`() {
        val foo = subclass("FooDestination", "/foo")
        val root = sealedRoot("com.example", "MainDestination", listOf(foo))
        val resolver = resolverFor(root)

        val codeGenerator =
            mockk<CodeGenerator> {
                every { createNewFile(any(), any(), any(), any()) } returns ByteArrayOutputStream()
            }
        val logger = mockk<KSPLogger>(relaxed = true)
        val processor = WebDestinationProcessor(codeGenerator, logger)

        processor.process(resolver)
        processor.process(resolver)

        verify(exactly = 1) { codeGenerator.createNewFile(any(), any(), any(), any()) }
    }
}
