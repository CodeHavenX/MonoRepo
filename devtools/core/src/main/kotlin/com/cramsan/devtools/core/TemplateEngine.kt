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
 * Copies [src] to [dst] and applies [subs] as ordered find-and-replace pairs.
 * Substitutions are applied in list order, so earlier entries take precedence over later ones.
 */
fun applySubs(
    src: Path,
    dst: Path,
    subs: List<Pair<String, String>>,
) {
    dst.parent.createDirectories()
    var content = src.readText()
    for ((from, to) in subs) {
        content = content.replace(from, to)
    }
    dst.writeText(content)
}
