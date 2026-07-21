package com.tourian.rocketutils.objects

import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

enum class CelestialBody(
    val displayName: String,
    val radiusMeters: Double,
    val mu: Double, // Gravitational parameter (G * M)
    val color: Color
){
    KERBOL("Kerbol", 261_600_000.0,
        1.1723328e18, Color(0xFFE6D8B9)),
    MOHO("Moho", 250_000.0,
        1.6860938e11, Color(0xFFD60004)),
    EVE("Eve", 700_000.0,
        8.1717302e12, Color(0xFF611DCF)),
    GILLY("Gilly", 13_000.0,
        8_289_449.8, Color(0xFF917264)),
    KERBIN("Kerbin", 600_000.0,
        3.5316000e12, Color(0xFF3B82F6)),
    MUN("Mun", 200_000.0,
        6.5138398e10, Color(0xFF8B91A4)),
    MINMUS("Minmus", 60_000.0,
        1.7658000e9, Color(0xFF527955)),
    DUNA("Duna", 320_000.0,
        3.013621e11, Color(0xFF790002)),
    IKE("Ike", 130_000.0,
        1.8568369e10, Color(0xFF777D8D)),
    DRES("Dres", 138_000.0,
        2.1484489e10, Color(0xFF503E2E)),
    JOOL("Jool", 6_000_000.0,
        2.8252800e14, Color(0xFF4B7811)),
    LAYTHE("Laythe", 500_000.0,
        1.9620000e12, Color(0xFF3D4E8E)),
    VALL("Vall", 300_000.0,
        2.0748150e11, Color(0xFF628CA4)),
    TYLO("Tylo", 600_000.0,
        2.8252800e12, Color(0xFFBD9A9B)),
    BOP("Bop", 65_000.0,
        2.4868349e9, Color(0xFFA69173)),
    POL("Pol", 44_000.0,
        7.2170208e8, Color(0xFFC5CF9D)),
    EELOO("Eeloo", 210_000.0,
        7.4410815e10, Color(0xFF5D6061));


    fun calculateOrbitalPeriod(altitudeMeters: Double): Double {
        val semiMajorAxis = this.radiusMeters + altitudeMeters
        return 2 * PI * sqrt(semiMajorAxis.pow(3.0) / this.mu)
    }


}