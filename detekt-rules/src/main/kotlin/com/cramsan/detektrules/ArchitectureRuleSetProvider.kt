package com.cramsan.detektrules

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

/**
 * Provides the architecture rule set, which enforces layered architecture conventions
 * via the [AnnotationCallerRestrictionRule] and [ArchitectureNamingRule].
 */
class ArchitectureRuleSetProvider : RuleSetProvider {
    override val ruleSetId: RuleSetId = RuleSetId("architecture")

    override fun instance(): RuleSet =
        RuleSet(
            ruleSetId,
            listOf(::AnnotationCallerRestrictionRule, ::ArchitectureNamingRule),
        )
}
