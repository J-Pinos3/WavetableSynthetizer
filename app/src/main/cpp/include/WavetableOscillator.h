#pragma once

#include "AudioSource.h"


namespace wavetablesynthesizer{

    class A4Oscillator: public AudioSource{

    public:
        explicit A4Oscillator(float sampleRate);

        float getSample() override;

        void onPlaybackStopped() override;

    private:
        float _phase{0.f};
        float _phaseIncrement{0.f};

    };



}