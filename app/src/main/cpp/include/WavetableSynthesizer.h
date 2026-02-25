#pragma once

namespace wavetablesynthesizer{



    enum class Wavetable{
        SINE, TRIANGLE, SQUARE, SAW
    };

    class WavetableSynthesizer{
    public:
        void stop();
        bool isPlaying();
        void setFrequency(float frequencyInHz);
        void setVolume(float volumeInDb);
        void setWavetable(Wavetable wavetable);
        void play();

    private:
        bool _isPlaying = false;
    };


}