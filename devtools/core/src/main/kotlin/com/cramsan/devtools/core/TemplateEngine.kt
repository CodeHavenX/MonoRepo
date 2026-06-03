package com.cramsan.devtools.core

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.readText
import kotlin.io.path.writeText

/** Converts a dash-separated identifier to PascalCase. E.g. "my-app" → "MyApp". */
fun toPascal(input: String): String =
    input.split("-").joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }

/** Converts a PascalCase or UpperCase string to lowerCamelCase. E.g. "UserName" → "userName". */
fun toLowerCamel(input: String): String =
    input.replaceFirstChar(Char::lowercaseChar)

/**
 * Copies [src] to [dst] and applies the standard component substitutions in order:
 * 1. `TemplateReplaceMe` → [appPascal]
 * 2. `templatereplaceme` → [app]
 * 3. Each entry in [extraSubs] (e.g. `"Example"` → provider name)
 * 4. `ComponentReplaceme` → [name]
 * 5. `componentreplaceme` → [nameLower]
 */
fun applySubs(
    src: Path,
    dst: Path,
    appPascal: String,
    app: String,
    name: String,
    nameLower: String,
    extraSubs: Map<String, String> = emptyMap(),
) {
    dst.parent.createDirectories()
    var content = src.readText()
    content = content.replace("TemplateReplaceMe", appPascal)
    content = content.replace("templatereplaceme", app)
    for ((from, to) in extraSubs) {
        content = content.replace(from, to)
    }
    content = content.replace("ComponentReplaceme", name)
    content = content.replace("componentreplaceme", nameLower)
    dst.writeText(content)
}
