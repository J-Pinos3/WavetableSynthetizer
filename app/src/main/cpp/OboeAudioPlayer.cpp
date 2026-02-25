#include "OboeAudioPlayer.h"
#include "AudioSource.h"

using namespace oboe;

namespace wavetablesynthesizer{


    OboeAudioPlayer::OboeAudioPlayer( std::shared_ptr<AudioSource> source, int samplingRate  )
    :_source{std::move(source)}, _samplingRate{samplingRate}  {  }

    OboeAudioPlayer::~OboeAudioPlayer(){
        OboeAudioPlayer::stop();
    }

    int32_t OboeAudioPlayer::play() {
        AudioStreamBuilder builder;
        const auto result =
                builder.setPerformanceMode(PerformanceMode::LowLatency)
                ->setDirection(Direction::Output)
                ->setSampleRate(_samplingRate)
                ->setDataCallBack(this)
                ->setSharingMode(SharingMode::Exclusive)
                ->serFormat(AudioFormat::Float)
                ->setChannelCount(channelCount)
                ->setSampleRateConversionQuality(SampleRateConversionQuality::Best)
                ->openStream(_stream);

        if(result != result::OK){
            return static_cast<int32_t>(result);
        }

        const auto playResult = _stream->RequestStart();

        return static_cast<int32_t>(playResult);
    }

    void OboeAudioPlayer::stop() {
        if(_stream){
            _stream->stop();
            _stream->close();
            _stream.reset();

        }

        _source->onPlaybackStopped();
    }

    oboe::DataCallbackResult OboeAudioPlayer::onAudioReady(oboe::AudioStream* audioStream,
                                          void* audioData,
                                          int32_t framesCount){

        auto* floatData = reinterpret_cast<float*>(audioData);

        for(auto frame = 0; frame < framesCount; frame++){

        }
    }

}