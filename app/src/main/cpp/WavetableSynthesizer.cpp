#include "include/Log.h"
#include "include/WavetableSynthesizer.h"
#include "OboeAudioPlayer.h"
#include <cmath>
#include "WavetableOscillator.h"

namespace wavetablesynthesizer{

    WavetableSynthesizer::WavetableSynthesizer()
    :_oscillator{  std::make_shared<WavetableOscillator>(
            _wavetableFactory.getWaveTable(_currentWavetable), sampleRate    )    },
    _audioPlayer{ std::make_unique<OboeAudioPlayer>(_oscillator, sampleRate) }
    {}

    WavetableSynthesizer::~WavetableSynthesizer() = default;


    void WavetableSynthesizer::play(){

        std::lock_guard<std::mutex> lock(_mutex);

        const auto result  = _audioPlayer->play();
        if(result == 0){
            _isPlaying = true;
        }else{
            LOGD("could not start playback");
        }

    }

    void WavetableSynthesizer::stop(){

        std::lock_guard<std::mutex> lock(_mutex);
        _audioPlayer->stop();
        _isPlaying = false;
    }

    bool WavetableSynthesizer::isPlaying() const{
        LOGD("isPlaying() called");
        return _isPlaying;
    }

    void WavetableSynthesizer::setFrequency(float frequencyInHz){
        _oscillator->setFrequency(frequencyInHz);
    }

    float dbToAmplitude(float deciBels){
        return std::pow(10.f, deciBels/20.f);
    }

    void WavetableSynthesizer::setVolume(float volumeInDb){
        const float amplitude = dbToAmplitude(volumeInDb);
        _oscillator->setAmplitude(amplitude);
    }

    void WavetableSynthesizer::setWavetable(Wavetable wavetable){
        if(_currentWavetable != wavetable){
            _currentWavetable = wavetable;
            _oscillator->setWavetable(_wavetableFactory.getWaveTable(wavetable));
        }
    }



}

