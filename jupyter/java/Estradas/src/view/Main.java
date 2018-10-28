package view;

import controller.ControllerApp;
import java.sql.Connection;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		ControllerApp appLista = new ControllerApp();
		Connection con = appLista.conectarDatabase();
		
		if (con != null) {
			appLista.eliminarTabelas(con);
			appLista.crearTabelaTask(con);
			appLista.crearTabelaDependency(con);
			
			appLista.obterTarefas(con);
			appLista.obterDependencias(con);
			
			appLista.fecharConecaoDatabase(con);
			
			
			List<String> caminho = appLista.caminhoTarefas("Preparação", "Fatura");
			if (!caminho.isEmpty()) {
				for (int i = 0; i < caminho.size(); i++) {
					if (i < caminho.size()-1) System.out.print(caminho.get(i) + "-");
					else System.out.println(caminho.get(i));
				}
			}
			else System.out.println("Iserir valores validos");
			
			System.out.println("Tamanho do caminho: " + appLista.tamanhoCaminho(caminho));
			System.out.println("Tempo do caminho: " + appLista.tempoCaminho(caminho));
		}
		
	}

}
