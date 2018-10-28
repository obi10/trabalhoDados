package controller;

import model.Task;
import model.Dependency;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class ControllerApp
{
	public List<Task> listaTarefas;
	public List<Dependency> listaDependencias;
	
	public ControllerApp() {
		this.listaTarefas = new ArrayList<Task>();
		this.listaDependencias = new ArrayList<Dependency>();
	}
	
	public Connection conectarDatabase() {
		Connection con = null;
		try {
			// carrega classe com implementacao para o driver
			//   neste caso o driver MySQL
			Class.forName("org.h2.Driver");
			// estabelece conexao com a base de dados
			con = DriverManager.getConnection("jdbc:h2:./database/estradas/estradas");
			System.out.println("Database - conecao estabelecida");
		} catch (ClassNotFoundException erro) {
			System.out.println("ClassNotFoundException: " + erro.getMessage());
		} catch (SQLException erro) {
			System.out.println("Erro no SQL: " + erro.getMessage());
		}
		return con;
	}
	  
	public void fecharConecaoDatabase(Connection con) {
		try {
			con.close();
		} catch (SQLException erro) {
			System.out.println("Erro no SQL: " + erro.getMessage());
		}
	}
	  
	public void eliminarTabelas(Connection con)
	{
		try {
			Statement stmt = con.createStatement();
			String sql = "DROP TABLE IF EXISTS Tasks;" +
            			 "DROP TABLE IF EXISTS Dependency;";
            stmt.execute(sql);  
            stmt.close();
		} catch (SQLException erro) {
            System.out.println("Erro no SQL: " + erro.getMessage());
        }
	}

	public void crearTabelaTask(Connection con)
	{
		try {
			Statement stmt = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS Tasks " + 
            				"(id VARCHAR(20) NOT NULL, " +
            				"name VARCHAR(100) NULL, " +
            				"PRIMARY KEY(id)) " + 
            				"AS SELECT id, name FROM CSVREAD('./tables/task.csv');";
            stmt.execute(sql);
            System.out.println("Tabela Tasks creada");
            
            stmt.close();
		} catch (SQLException erro) {
            System.out.println("Erro no SQL: " + erro.getMessage());
        }
	}
      
	public void crearTabelaDependency(Connection con)
	{
		try {
			Statement stmt = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS Dependency " + 
            				"(before VARCHAR(20) NOT NULL, " +
            				"after VARCHAR(20) NOT NULL, " +
            				"time INTEGER NOT NULL, " +
            				"PRIMARY KEY(before, after), " +
            				"FOREIGN KEY(before) REFERENCES Tasks(id), " +
            				"FOREIGN KEY(after) REFERENCES Tasks(id)" +
            				") AS SELECT before, after, time FROM CSVREAD('./tables/dependency.csv');";
            stmt.execute(sql);
            System.out.println("Tabela Dependency creada");
            
            stmt.close();
		} catch (SQLException erro) {
            System.out.println("Erro no SQL: " + erro.getMessage());
        }
	}
      
	public void obterTarefas(Connection con)
	{
		try {
			Statement stmt = con.createStatement();
            String sql = "SELECT * FROM Tasks";
            ResultSet resultado = stmt.executeQuery(sql);
            
            System.out.println("===== Tasks =====");
            System.out.println("id, name");
            
            // lista o conteudo da tabela no console
            boolean temConteudo = resultado.next();
            while (temConteudo)
            {
            	String idTask = resultado.getString("id");
            	String nameTask = resultado.getString("name");
            	
            	Task novaTarefa = new Task(idTask, nameTask);
            	this.listaTarefas.add(novaTarefa);
            	
            	System.out.println(idTask + ", " + nameTask);
            	temConteudo = resultado.next();
            }
            
            stmt.close();
        } catch (SQLException erro) {
            System.out.println("Erro no SQL: " + erro.getMessage());
        }
	}
	public void obterDependencias(Connection con)
	{
		try {
			Statement stmt = con.createStatement();
            String sql = "SELECT * FROM Dependency";
            ResultSet resultado = stmt.executeQuery(sql);
            
            System.out.println("===== Dependency =====");
            System.out.println("before, after, time");
            
            // lista o conteudo da tabela no console
            boolean temConteudo = resultado.next();
            while (temConteudo)
            {
            	String beforeDependency = resultado.getString("before");
            	String afterDependency = resultado.getString("after");
            	int timeDependency = resultado.getInt("time");
            	
            	Dependency novaDependencia = new Dependency(beforeDependency, afterDependency, timeDependency);
            	this.listaDependencias.add(novaDependencia);
            	
            	System.out.println(beforeDependency + ", " + afterDependency + ", " + timeDependency);
            	temConteudo = resultado.next();
            }
            
            stmt.close();
		} catch (SQLException erro) {
			System.out.println("Erro no SQL: " + erro.getMessage());
        }
	}
      
      
    private boolean verificarTarefas(String tarefaInicio, String tarefaFim) {
    	boolean auxTarefaInicio = false, auxTarefaFim = false;
		for (int i = 0; i < this.listaTarefas.size(); i++) {
			if (this.listaTarefas.get(i).id.equals(tarefaInicio)) {
				auxTarefaInicio = true;
				break;
			}
		}
		for (int i = 0; i < this.listaTarefas.size(); i++) {
			if (this.listaTarefas.get(i).id.equals(tarefaFim)) {
				auxTarefaFim = true;
				break;
			}
		}
    	return (auxTarefaInicio && auxTarefaFim);
    }
	
    private List<String> obterProximasTarefas(String tarefa){
    	List<String> listaProximasTarefas = new ArrayList<String>();
    	
    	for (int i = 0; i < this.listaDependencias.size(); i++) {
    		if (this.listaDependencias.get(i).before.equals(tarefa))
    			listaProximasTarefas.add(this.listaDependencias.get(i).after);
		}
    	
    	return listaProximasTarefas;
    }
    
	//caminho entre dois tarefas
	public List<String> caminhoTarefas(String tarefaInicio, String tarefaFim) {
		List<String> caminho = new ArrayList<String>();
		if (this.verificarTarefas(tarefaInicio, tarefaFim)) {
			int count = 0;
			String tarefa_aux = tarefaInicio;
			//List<String> listaTarefasBifu = new ArrayList<String>();
			
			List<String> listaProximasTarefas = null;
			while(true){
				caminho.add(tarefa_aux);
				if (tarefa_aux.equals(tarefaFim)) break;
				if (tarefa_aux.equals("Fim")) { //nao se encontrou a tarefaFim
					caminho.clear();
					tarefa_aux = tarefaInicio;
					caminho.add(tarefa_aux);
					count ++;
				}
				listaProximasTarefas = this.obterProximasTarefas(tarefa_aux);
				if (listaProximasTarefas.size() > 1) {
					//listaTarefasBifu.add(tarefa_aux);
					tarefa_aux = listaProximasTarefas.get(count);
				}
				else tarefa_aux = listaProximasTarefas.get(0);
			}
			
		}
		return caminho;
	}
      
	public int tamanhoCaminho(List<String> caminho) {
		return (caminho.size()-1);
	}
	
	public int tempoCaminho(List<String> caminho) {
		int tempo = 0;
		int c = 0;
		while(true){
			for (int i = 0; i < this.listaDependencias.size(); i++) {
				if (this.listaDependencias.get(i).before.equals(caminho.get(c)) && 
						this.listaDependencias.get(i).after.equals(caminho.get(c+1))) {
					tempo = tempo + this.listaDependencias.get(i).time;
					c ++;
					break;
				}
			}
			if (c == caminho.size()-1) break;
		}
		return tempo;
	}
      
}
