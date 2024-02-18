package viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.Bird

data class BirdsUiState(
    val images : List<Bird> = emptyList()
)

class BirdViewModel : ViewModel()
{
    private val _uiState : MutableStateFlow<BirdsUiState> = MutableStateFlow(BirdsUiState())
    var uiState : StateFlow<BirdsUiState> = _uiState

    init {
        updateImages()
    }

    private fun updateImages()
    {
        viewModelScope.launch {
            val images = getImages()
            _uiState.update {
                it.copy(images = images)
            }
        }
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation)
        {
            json()
        }
    }

    suspend fun getImages() : List<Bird>{
        val images = httpClient.get("https://sebi.io/demo-image-api/pictures.json")
            .body<List<Bird>>()
        return images
    }


}