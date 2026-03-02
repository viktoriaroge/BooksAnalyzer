package com.viroge.booksanalyzer.ui.common.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * NOTE: How to use guide.
 * {spe}text{/spe} -> Special, make it bold and color it with the tertiary color.
 * {err}text{/err} -> Error, make it bold and color it with the error color.
 * {bol}text{/bol} -> Bold, make it bold.
 *
 * NOTE: How nesting works.
 * The innermost would win. So however you wrap it, the inner tag would carry the verdict.
 * If you need specialised tags with style and color and what-not, add a tag (3-char-limit).
 *
 * Why? Because the HTML options are just too annoying and complex to handle.
 * Just add your string with its references (not to worry about those one bit),
 * then call this function and enjoy!
 */
@Composable
fun customAnnotatedString(id: Int, vararg formatArgs: Any): AnnotatedString {
    val rawText = stringResource(id, *formatArgs)

    val boldStyle = SpanStyle(
        fontWeight = FontWeight.ExtraBold,
    )
    val errorStyle = SpanStyle(
        color = MaterialTheme.colorScheme.error,
        fontWeight = FontWeight.Bold,
    )
    val specialStyle = SpanStyle(
        color = MaterialTheme.colorScheme.tertiary,
        fontWeight = FontWeight.ExtraBold,
    )

    return buildAnnotatedString {
        // Matches {xxx} or {/xxx} where x is any letter
        val pattern = Regex("\\{[a-z]{3}\\}|\\{/[a-z]{3}\\}")
        val parts = rawText.split(pattern)
        val matches = pattern.findAll(rawText).toList()

        var currentPartIndex = 0
        val styleStack = mutableListOf<SpanStyle>()

        parts.forEach { text ->
            if (styleStack.isNotEmpty()) {
                var combinedStyle = SpanStyle()
                styleStack.forEach { combinedStyle = combinedStyle.merge(it) }
                withStyle(combinedStyle) { append(text) }
            } else {
                append(text)
            }

            if (currentPartIndex < matches.size) {
                val tag = matches[currentPartIndex].value
                when (tag) {
                    "{bol}" -> styleStack.add(boldStyle)
                    "{err}" -> styleStack.add(errorStyle)
                    "{spe}" -> styleStack.add(specialStyle)
                    // Closing tags: just pop the last style off the stack
                    "{/bol}", "{/err}", "{/spe}" -> {
                        if (styleStack.isNotEmpty()) styleStack.removeAt(styleStack.size - 1)
                    }
                }
                currentPartIndex++
            }
        }
    }
}
