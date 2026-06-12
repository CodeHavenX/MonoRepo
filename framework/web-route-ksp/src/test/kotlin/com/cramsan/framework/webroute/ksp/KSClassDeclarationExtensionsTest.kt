package com.cramsan.framework.webroute.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KSClassDeclarationExtensionsTest {
    private fun nameMock(value: String): KSName = mockk { every { asString() } returns value }

    private fun classWithQualifiedName(
        qualifiedName: String,
        superTypes: Sequence<KSTypeReference> = emptySequence(),
    ): KSClassDeclaration =
        mockk {
            every { this@mockk.qualifiedName } returns nameMock(qualifiedName)
            every { this@mockk.superTypes } returns superTypes
        }

    private fun typeReferenceTo(declaration: KSClassDeclaration): KSTypeReference {
        val type = mockk<KSType> { every { this@mockk.declaration } returns declaration }
        return mockk { every { resolve() } returns type }
    }

    @Test
    fun `implementsWebDestination is true when the class directly is WebDestination`() {
        val declaration = classWithQualifiedName(WEB_DESTINATION_FQN)

        assertTrue(declaration.implementsWebDestination())
    }

    @Test
    fun `implementsWebDestination is true via a supertype chain`() {
        val webDestination = classWithQualifiedName(WEB_DESTINATION_FQN)
        val declaration =
            classWithQualifiedName(
                "com.example.MainDestination",
                superTypes = sequenceOf(typeReferenceTo(webDestination)),
            )

        assertTrue(declaration.implementsWebDestination())
    }

    @Test
    fun `implementsWebDestination is false when no supertype matches`() {
        val any = classWithQualifiedName("kotlin.Any")
        val declaration =
            classWithQualifiedName(
                "com.example.NotADestination",
                superTypes = sequenceOf(typeReferenceTo(any)),
            )

        assertFalse(declaration.implementsWebDestination())
    }

    @Test
    fun `webPathOrNull returns the path when annotated with WebPath`() {
        val pathArg =
            mockk<KSValueArgument> {
                every { name } returns nameMock("path")
                every { value } returns "/archive"
            }
        val resolvedAnnotationType =
            mockk<KSType> {
                every { declaration } returns classWithQualifiedName(WEB_PATH_FQN)
            }
        val annotation =
            mockk<KSAnnotation> {
                every { annotationType } returns
                    mockk<KSTypeReference> { every { resolve() } returns resolvedAnnotationType }
                every { arguments } returns listOf(pathArg)
            }
        val declaration =
            mockk<KSClassDeclaration> {
                every { annotations } returns sequenceOf(annotation)
            }

        assertEquals("/archive", declaration.webPathOrNull())
    }

    @Test
    fun `webPathOrNull returns null when no annotation is present`() {
        val declaration =
            mockk<KSClassDeclaration> {
                every { annotations } returns emptySequence()
            }

        assertNull(declaration.webPathOrNull())
    }

    @Test
    fun `webPathOrNull returns null when an unrelated annotation is present`() {
        val resolvedAnnotationType =
            mockk<KSType> {
                every { declaration } returns classWithQualifiedName("kotlin.Suppress")
            }
        val annotation =
            mockk<KSAnnotation> {
                every { annotationType } returns
                    mockk<KSTypeReference> { every { resolve() } returns resolvedAnnotationType }
                every { arguments } returns emptyList()
            }
        val declaration =
            mockk<KSClassDeclaration> {
                every { annotations } returns sequenceOf(annotation)
            }

        assertNull(declaration.webPathOrNull())
    }
}
