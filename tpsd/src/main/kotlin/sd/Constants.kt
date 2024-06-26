@file:Suppress("MagicNumber")

package sd

val INITIAL_POSITIVE_PROMPT = listOf(
    Term("end of the world"),
    Term("epic realistic"),
    Term("hdr", 3),
    Term("muted colors", 4),
    Term("apocalypse"),
    Term("freezing"),
    Term("abandoned"),
    Term("neutral colors"),
    Term("night"),
    Term("screen space refractions"),
    Term("intricate details", 2),
    Term("hyperdetailed", 2),
    Term("artstation"),
    Term("cinematic shot"),
    Term("vignette"),
    Term("complex background"),
    Term("buildings"),
    Term("snowy"),
)

val INITIAL_NEGATIVE_PROMPT = mutableListOf(
    Term("poorly"),
    Term("deformed"),
    Term("distorted"),
    Term("disfigured"),
    Term("poorly drawn"),
    Term("bad anatomy"),
    Term("wrong anatomy"),
    Term("extra limb"),
    Term("missing limb"),
    Term("floating limbs"),
    Term("disconnected limbs"),
    Term("mutation"),
    Term("mutated"),
    Term("ugly"),
    Term("disgusting"),
    Term("blurry"),
    Term("amputation"),
)
