package com.tourian.rocketutils.objects

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

enum class CelestialBody(
    val displayName: String,
    val radiusMeters: Double,
    val mu: Double // Gravitational parameter (G * M)
){
    KERBOL("Kerbol", 261_600_000.0, 1.1723328e18),
    MOHO("Moho", 250_000.0, 1.6860938e11),
    EVE("Eve", 700_000.0, 8.1717302e12),
    GILLY("Gilly", 13_000.0, 8_289_449.8),
    KERBIN("Kerbin", 600_000.0,3.5316000e12),
    MUN("Mun", 200_000.0, 6.5138398e10),
    MINMUS("Minmus", 60_000.0, 1.7658000e9),
    DUNA("Duna", 320_000.0, 3.013621e11),
    IKE("Ike", 130_000.0, 1.8568369e10),
    DRES("Dres", 138_000.0, 2.1484489e10),
    JOOL("Jool", 6_000_000.0, 2.8252800e14),
    LAYTHE("Laythe", 500_000.0, 1.9620000e12),
    VALL("Vall", 300_000.0, 2.0748150e11),
    TYLO("Tylo", 600_000.0, 2.8252800e12),
    BOP("Bop", 65_000.0, 2.4868349e9),
    POL("Pol", 44_000.0, 7.2170208e8),
    EELOO("Eeloo", 210_000.0, 7.4410815e10);


    fun calculateOrbitalPeriod(altitudeMeters: Double): Double {
        val semiMajorAxis = this.radiusMeters + altitudeMeters
        return 2 * PI * sqrt(semiMajorAxis.pow(3.0) / this.mu)
    }
}