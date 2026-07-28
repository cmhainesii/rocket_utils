package com.tourian.rocketutils.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourian.rocketutils.data.MissionPlanDao
import com.tourian.rocketutils.data.MissionPlanEntity
import com.tourian.rocketutils.objects.CelestialBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MissionLogViewModel(
    private val missionDao: MissionPlanDao
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow<CelestialBody?>(null)
    val selectedFilter: StateFlow<CelestialBody?> = _selectedFilter.asStateFlow()

    // Temporary storage for undoing a delete action
    private var recentlyDeletedMission: MissionPlanEntity? = null

    // Reactive list combining Room data, active search query, and filter chip selection
    val missions: StateFlow<List<MissionPlanEntity>> = combine(
        missionDao.getAllMissions(),
        _searchQuery,
        _selectedFilter

    ) { allMissions, query, filter ->
        allMissions.filter { mission ->
            val matchesQuery = mission.missionName.contains(query, ignoreCase = true) ||
                    mission.bodyName.contains(query, ignoreCase = true)

            val matchesFilter = filter == null ||
                    mission.bodyName.equals(filter.displayName, ignoreCase = true)

            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterSelect(body: CelestialBody?) {
        // Tapping the active filter turns it off
        _selectedFilter.value = if (_selectedFilter.value == body) null else body
    }

    fun deleteMission(mission: MissionPlanEntity) {
        recentlyDeletedMission = mission
            viewModelScope.launch {
                missionDao.deleteMission(mission    )
            }
    }

    fun undoDelete() {
        recentlyDeletedMission?.let { mission ->
            viewModelScope.launch {
                missionDao.insertMission(mission)
                recentlyDeletedMission = null
            }
        }
    }

}