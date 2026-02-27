#include "WavetableFactory.h"

#include "Wavetable.h"

#include <cmath>
#include "MathConstants.h"

namespace {
    constexpr  auto  WAVETABLE_LENGTH = 256;


    std::vector<float> generateSineWaveTable(){
        //it computes the values of a single period of a sine wave
        auto sineWaveTable = std::vector<float>(WAVETABLE_LENGTH);
        for( auto i = 0; i < WAVETABLE_LENGTH; i++ ){
            sineWaveTable[i] = std::sin(2 * wavetablesynthesizer::PI * static_cast<float>(i) / WAVETABLE_LENGTH );

        }

        return sineWaveTable;
    }

    std::vector<float> generateTriangleWaveTable(){
        auto triangleWaveTable = std::vector<float>(WAVETABLE_LENGTH, 0.f);

        constexpr auto HARMONICS_COUNT = 13;

        for(auto k = 1; k <= HARMONICS_COUNT; k++){
            for(auto j = 0; j < WAVETABLE_LENGTH; j++  ){
                const auto phase = 2.f *  wavetablesynthesizer::PI * j / WAVETABLE_LENGTH;

                triangleWaveTable[j] += 8.f / std::pow( wavetablesynthesizer::PI, 2.f )
                        * std::pow(-1.f, k) * std::pow(2 * k - 1, -2.f)
                        * std::sin( (2.f * k -1.f) * phase);
            }
        }

        return triangleWaveTable;
    }


    //pass a function to another function
    template <typename F>
    std::vector<float> generateWaveTableOnce(std::vector<float>& waveTable, F&& generator  ){
        if(waveTable.empty()){
            waveTable = generator();
        }

        return waveTable;
    }

}

namespace  wavetablesynthesizer{

    std::vector<float> WavetableFactory::getWaveTable(Wavetable wavetable){
        switch ( wavetable ) {
            case Wavetable::SINE: {
                return sineWaveTable();
            }

            case Wavetable::SQUARE: {
                return squareWaveTable();
            }

            case Wavetable::TRIANGLE: {
                return triangleWaveTable();
            }

            case Wavetable::SAW: {
                return sawWaveTable();
            }

            default:{
                return std::vector<float>(WAVETABLE_LENGTH, 0.f);
            }
        }
    }



    std::vector<float> WavetableFactory::sineWaveTable(){
        return generateWaveTableOnce( _sineWaveTable, &generateSineWaveTable );
    }

    std::vector<float> WavetableFactory::triangleWaveTable(){
        return generateWaveTableOnce( _triangleWaveTable, &generateTriangleWaveTable );
    }

    std::vector<float> WavetableFactory::squareWaveTable(){
        return {};
    }

    std::vector<float> WavetableFactory::sawWaveTable(){
        return {};
    }



}