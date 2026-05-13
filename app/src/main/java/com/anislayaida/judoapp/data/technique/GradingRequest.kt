package com.anislayaida.judoapp.data.technique

data class GradingRequest(
    val id:              String = "",
    val judokaUid:       String = "",
    val judokaName:      String = "",
    val currentBelt:     String = "",
    val requestedBelt:   String = "",
    val status:          String = "pending",    
    val stage:           String = "readiness",  
    val rejectionReason: String = "",
    val cooldownUntil:   Long   = 0L,
    val timestamp:       Long   = System.currentTimeMillis()
)