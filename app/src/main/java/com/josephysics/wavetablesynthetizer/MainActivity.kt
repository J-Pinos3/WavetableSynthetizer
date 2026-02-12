package com.josephysics.wavetablesynthetizer

import android.content.pm.ActivityInfo
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josephysics.wavetablesynthetizer.ui.theme.WavetableSynthetizerTheme

class MainActivity : ComponentActivity() {
    private val synthesizerViewModel: WavetableSynthesizerViewModel by viewModels()
    private val synthesizer = LoggingWavetableSynthesizer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        synthesizerViewModel.wavetableSynthesizer = synthesizer
        setContent {
            WavetableSynthetizerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    WavetableSynthetizerApp(Modifier,synthesizerViewModel)
                }
            }
        }
    }

    override fun onResume(){
        super.onResume()
        synthesizerViewModel.applyParameters()//make sure viewmodel remember values
    }

}

@Composable
fun WavetableSynthetizerApp(
    modifier: Modifier,
    synthesizerViewModel: WavetableSynthesizerViewModel
){
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        WavetableSelectionPanel(modifier,synthesizerViewModel)
        ControlsPanel(modifier, synthesizerViewModel)
    }
}


@Composable
fun WavetableSelectionPanel(
    modifier: Modifier,
    synthesizerViewModel: WavetableSynthesizerViewModel
){
    Row(
       modifier = modifier.fillMaxWidth()
           .fillMaxHeight(0.5f),//0.5f
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(stringResource(R.string.wavetable))
            WavetableSelectionButtons(modifier, synthesizerViewModel)
        }
    }
}


@Composable
fun WavetableSelectionButtons(
    modifier: Modifier,
    synthesizerViewModel: WavetableSynthesizerViewModel
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        for(wavetable in Wavetable.values() ){
            WavetableButton(
                modifier = modifier,
                onClick = { synthesizerViewModel.setWavetable(wavetable) },
                label =  stringResource( wavetable.toResourceString() )
            )
        }
    }
}


@Composable
fun WavetableButton(
    modifier: Modifier,
    onClick: () -> Unit,
    label: String
){
    Button(
        modifier = modifier, onClick = onClick
    ) {
        Text(label)
    }

}


@Composable
fun ControlsPanel(
    modifier: Modifier,
    synthesizerViewModel: WavetableSynthesizerViewModel
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = modifier
                .fillMaxHeight()
                .fillMaxWidth(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            PitchControl(modifier, synthesizerViewModel)
            PlayControl(modifier, synthesizerViewModel)
        }


        Column(//30% of the row
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            VolumeControl(modifier, synthesizerViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PitchControl(
    modifier: Modifier,
    synthesizerViewModel: WavetableSynthesizerViewModel
){
    val frequency = synthesizerViewModel.frequency.observeAsState()

    PitchControlContent(
        modifier = modifier,
        pitchControlLabel =  stringResource(R.string.frequency),
        value = synthesizerViewModel.sliderPositionFromFrequencyInHz(frequency.value!!),
        onValueChange = {
            synthesizerViewModel.setFrequencySliderPosition(it)
        },
        valueRange = 0F..1F,//40F..3100F,
        frequencyValueLabel = stringResource(R.string.frequency_value, frequency.value!!)
    )
}

@Composable
fun PitchControlContent(
    modifier: Modifier,
    pitchControlLabel: String,
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    frequencyValueLabel: String
){
    Text(pitchControlLabel)

    Slider(modifier = modifier, value = value, onValueChange = onValueChange, valueRange = valueRange  )

    Text( frequencyValueLabel )
}


@Composable
fun PlayControl(
    modifier: Modifier,
    synthesizerViewModel: WavetableSynthesizerViewModel
){

    val playButtonLabel = synthesizerViewModel.playButtonLabel.observeAsState()

    Button(
        modifier = modifier,
        onClick = {
            synthesizerViewModel.playClicked()
        }
    ){
        Text( stringResource(playButtonLabel.value!!) )
    }
}


@Composable
fun VolumeControl(
    modifier: Modifier,
    synthesizerViewModel: WavetableSynthesizerViewModel
){
    val volume = synthesizerViewModel.volume.observeAsState()

    VolumeControlContent(
        modifier = modifier,
        volumeValue = volume.value!!,
        onValueChange = { synthesizerViewModel.setVolume(it) },
        valueRange = synthesizerViewModel.volumeRange
    )
}


@Composable
fun VolumeControlContent(
    modifier: Modifier,
    volumeValue: Float,
    onValueChange: (Float)-> Unit,
    valueRange: ClosedFloatingPointRange<Float>
){
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val sliderHeight = screenHeight/4

    Icon(imageVector = Icons.Filled.VolumeUp, contentDescription = null)
    Slider(
        value = volumeValue,
        onValueChange = onValueChange,
        modifier = modifier.width(sliderHeight.dp).rotate(270f),
        valueRange = valueRange//-dB
    )
    Icon(imageVector = Icons.Filled.VolumeMute, contentDescription = null)
}

