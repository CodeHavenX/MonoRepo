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
 * Applies [subs] as ordered find-and-replace pairs to [content] and returns the result.
 * Earlier entries take precedence because they are applied first.
 */
fun applySubsToContent(content: String, subs: List<Pair<String, String>>): String {
    var result = content
    for ((from, to) in subs) {
        result = result.replace(from, to)
    }
    return result
}

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
    dst.writeText(applySubsToContent(src.readText(), subs))
}
