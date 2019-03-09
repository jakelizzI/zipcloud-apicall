package sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;

/**
 * 郵便番号から住所などをただAPIをコールして、都道府県を取得するだけ。
 * 出力は標準出力のみ。
 */
public class APICall {

	public static void main(String[] args) {
		Optional<JsonNode> nodeOpt = getJsonNode(args);

		String prefectures = nodeOpt
							// ここの無理矢理感がやばいｗ
							.map( node -> node.get("results").get(0).get("address1").toString())
							.orElse("");

		System.out.println(prefectures);
	}

	/**
	 * 引数を結合する。（メソッド化の必要ないｗ）
	 *
	 * @param args 郵便番号前提
	 * @return 111 2222 などが入ってきても 1112222などに結合された値が入る。
	 */
	private static String checkArgs(String[] args) throws Exception {

		String rtValue = String.join("",args);

		// 7桁以外は許さない
		if(rtValue.length() != 7){
			// 何らかのExceptionを投げる
			throw new Exception(rtValue);
		}
		return rtValue;
	}

	/**
	 * APIコール部分
	 *
	 * @param postNo 郵便番号
	 * @return 結果のJSON
	 */
	private static String callAPI(String postNo) {
		OkHttpClient client = new OkHttpClient();

		// 必ずargには7桁の数字が入っているのでここでGETリクエストを作る
		String url = "http://zipcloud.ibsnet.co.jp/api/search?zipcode=" + postNo;

		Request req = new Request.Builder().url(url).build();

		String responseString = "";
		try {
			Response res = client.newCall(req).execute();
			assert res.body() != null;
			responseString =  res.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseString;
	}

	/**
	 * JsonNodeを作るメソッド
	 *
	 * @param args メインメソッドの引数
	 * @return APIコールした結果のJsonNode
	 */
	private static Optional<JsonNode> getJsonNode(String[] args) {

		String postNo = null;
		try {
			postNo = checkArgs(args);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String json = callAPI(postNo);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = null;
		try {
			node = objectMapper.readTree(json);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Optional.ofNullable(node);
	}
}
