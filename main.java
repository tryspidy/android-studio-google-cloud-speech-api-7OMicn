> The audiofile is called "raw.wav" and is located under the resources directory
> Under the path "/raw/raw.wav"
> Credentials path is under "/raw/credentials.json"
>



~~~~build.gradle:
dependincies
{
	//...
    
    implementation 'com.google.cloud:google-cloud-speech:1.27.1'
    implementation 'io.grpc:grpc-okhttp:1.37.0'
    implementation 'io.grpc:grpc-protobuf:1.37.0'
    implementation 'io.grpc:grpc-stub:1.37.0'
    compileOnly 'org.apache.tomcat:annotations-api:6.0.53' // necessary for Java 9+
}

~~~~Code:

public class SpeechToText {
  	final int AUDIO_FILE_ID = R.raw.raw;
  final int CREDENTIALS_FILE_ID = R.raw.credentials;
	SpeechToText(Activity activity) throws Exception {

        SpeechClient speech = null;

        try {
            InputStream credentialsStream = activity.getResources().openRawResource(CREDENTIALS_FILE_ID);
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            SpeechSettings speechSettings =
                    SpeechSettings.newBuilder()
                            .setCredentialsProvider(credentialsProvider)
                            .build();

             speech = SpeechClient.create(speechSettings);


            InputStream is = activity.getResources().openRawResource(AUDIO_FILE_ID);

            byte[] bytes = new byte[is.available()];
            if(is.read(bytes) == 0)
            {
                throw new Exception("Didn't Read bytes from resource");
            }

            ByteString audioBytes = ByteString.copyFrom(bytes);

            // Builds the sync recognize request
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(AudioEncoding.LINEAR16)
                            .setSampleRateHertz(44100)
                            .setLanguageCode("en-US")
                            .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
                for (SpeechRecognitionAlternative alternative : alternatives) {
                    Log.d("Transcription: ", alternative.getTranscript()); //answer
                }
            }
            speech.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}