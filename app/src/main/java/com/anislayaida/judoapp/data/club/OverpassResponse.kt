package com.anislayaida.judoapp.data.club

data class OverpassResponse(
    val elements: List<OverpassElement>
)

data class OverpassElement(
    val type: String,
    val id: Long,
    val tags: OverpassTags?
)

data class OverpassTags(
    val name: String?,
    val sport: String?
)