package telegrambot.apiclients.speechtotext;

import com.assemblyai.api.AssemblyAI;
import com.assemblyai.api.resources.transcripts.types.*;

public final class App {
	private static final String API_KEY = System.getenv("ASSEMBLY_API_KEY");
	private static final String AUDIO_URL = "https://storage.googleapis.com/aai-web-samples/5_common_sports_injuries.mp3";

	public static void main(String[] args) {
		transcribeAudioToText();
	}



	private static void transcribeAudioToText() {
		AssemblyAI client = AssemblyAI.builder()
				.apiKey(API_KEY)
				.build();


		var params = TranscriptOptionalParams.builder()
				.speakerLabels(true)
				.build();

		Transcript transcript = client.transcripts().transcribe(AUDIO_URL, params);

		System.out.println(transcript.getText());

		transcript.getUtterances().ifPresent(utterances ->
				utterances.forEach(utterance ->
						System.out.println("Speaker " + utterance.getSpeaker() + ": " + utterance.getText())
				)
		);
	}
}