package com.example.model

data class StyleOption(
    val title: String,
    val promptModifier: String
)

data class FramingOption(
    val title: String,
    val promptModifier: String
)

data class LightingOption(
    val title: String,
    val promptModifier: String
)

data class AspectRatioOption(
    val ratioLabel: String,
    val description: String,
    val width: Int,
    val height: Int,
    val aspectFloat: Float
)

object CyberPresets {
    val styles = listOf(
        StyleOption(
            title = "Cuerpo Sintético",
            promptModifier = "synthetic full body, android biomechanical model, highly intricate, hyperrealistic, octane render"
        ),
        StyleOption(
            title = "Retrato Realista",
            promptModifier = "photorealistic portrait, high fashion editorial, studio depth, 8k, pores texture, canon eos r5"
        ),
        StyleOption(
            title = "Cyborg Sci-Fi",
            promptModifier = "futuristic sci-fi cyborg, gleaming chrome implants, glowing LED circuits, mecha chassis"
        ),
        StyleOption(
            title = "Render 3D / CGI",
            promptModifier = "3D CGI character, pixar stylized render, unreal engine 5, raytracing, vibrant subsurface scattering"
        ),
        StyleOption(
            title = "Anime Master",
            promptModifier = "modern anime aesthetic, makoto shinkai visual vibes, sharp clean line art, masterwork"
        ),
        StyleOption(
            title = "Fantasía Épica",
            promptModifier = "dark epic fantasy, mystical rune tattoos, ornate armor, ethereal particles, artstation"
        )
    )

    val framings = listOf(
        FramingOption("Cuerpo Completo", "full body shot, standing posed, head to toe visible"),
        FramingOption("Plano Medio", "medium shot, waist up composition, balanced posture"),
        FramingOption("Primer Plano", "extreme close-up face portrait, sharp eye focus"),
        FramingOption("Contrapicado", "dramatic low angle worm's-eye view, imposing perspective")
    )

    val lightings = listOf(
        LightingOption("Neón Cyberpunk", "cyberpunk dual neon violet and cyan rim lighting, volumetric glow"),
        LightingOption("Estudio Softbox", "studio softbox high-end lighting, neutral soft shadows"),
        LightingOption("Hora Dorada", "warm cinematic golden hour sunlight, lens flare"),
        LightingOption("Claroscuro", "dramatic baroque chiaroscuro, intense shadows, rembrandt lighting")
    )

    val aspectRatios = listOf(
        AspectRatioOption("9:16", "Vertical", 720, 1280, 9f / 16f),
        AspectRatioOption("1:1", "Cuadrado", 1024, 1024, 1f),
        AspectRatioOption("16:9", "Cine", 1280, 720, 16f / 9f)
    )

    val randomPrompts = listOf(
        "Femme fatale android assassin with glowing lavender irises, chrome spine plating, rainy neo-tokyo street reflection",
        "Cyborg warrior full body render in futuristic tactical nano-suit, floating energy crystals, ultra detailed",
        "High-fashion bio-mechanical model posing for cyberpunk vogue, carbon fiber joints, holographic jewelry",
        "Valkyrie gynoid with feathered titanium wings, pristine porcelain mask, hyperdetailed sci-fi cathedral background",
        "Solarpunk mechanical guardian with botanical brass gears, blooming flowers inside glass chest, vivid lighting",
        "Cyberpunk hacker operative with neon cyan optics, holographic neural interface visor, dark alley with holographic rain",
        "Neo-Tokyo synthetic idol with iridescent chromatic hair, sleek carbon fiber collar, pulsing LED tattoos"
    )
}
