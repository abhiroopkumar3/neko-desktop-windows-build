package eu.kanade.tachiyomi.data.database.models

import org.nekomanga.constants.Constants

/**
 * Desktop copy of upstream's MergeType enum (upstream keeps it in MergeMangaImpl.kt, which pulls in
 * every merged source). Ids, names and base urls must stay in sync with upstream; the desktop
 * source lookup (getSource) is added when the merged sources are ported.
 */
enum class MergeType(
    val id: Int,
    val scanlatorName: String,
    val baseUrl: String = "",
    val multiMerge: Boolean = false,
) {
    Invalid(id = -1, scanlatorName = "Invalid Merge source"),
    Komga(id = 1, scanlatorName = "Komga", multiMerge = true),
    Toonily(id = 2, scanlatorName = "Toonily", baseUrl = "https://toonily.com"),
    WeebCentral(id = 3, scanlatorName = "Weeb Central", baseUrl = "https://weebcentral.com"),
    Suwayomi(id = 5, scanlatorName = "Suwayomi", multiMerge = true),
    MangaBall(id = 7, scanlatorName = "Manga Ball", baseUrl = "https://mangaball.com"),
    ProjectSuki(id = 9, scanlatorName = "Project Suki", baseUrl = "https://projectsuki.com"),
    Comix(id = 10, scanlatorName = "Comix", baseUrl = "https://comix.to"),
    Atsumaru(id = 11, scanlatorName = "Atsumaru", baseUrl = "https://atsu.moe"),
    Kagane(id = 12, scanlatorName = "Kagane", baseUrl = "https://kagane.moe", multiMerge = true);

    fun mergeKey(url: String): Pair<MergeType, String?> = this to url.takeIf { multiMerge }

    companion object {
        fun getById(id: Int): MergeType = entries.firstOrNull { it.id == id } ?: Invalid

        fun getMergeTypeFromName(name: String?): MergeType? {
            val splitName = name?.split(Constants.SCANLATOR_SEPARATOR)?.firstOrNull()
            return entries.firstOrNull { it != Invalid && it.scanlatorName == splitName }
        }

        fun getMergeTypeName(mergeType: MergeType): String = mergeType.scanlatorName
    }
}
