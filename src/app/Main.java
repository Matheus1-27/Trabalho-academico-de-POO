
package app;

import services.SistemaService;
import views.Views;

public class Main {
	public static void main(String[] args) {
		Views.iniciar(new SistemaService());
	}
}
