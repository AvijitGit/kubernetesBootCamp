node{
    
    stage("Git Clone"){
        git 'https://github.com/AvijitGit/kubernetesBootCamp.git'
    }
    //Configure the maven with name Maven-Install in global configuration After checkout from git in the slave pod it will make two folder item-catalog and edge-service 
    stage("Maven Clean Build"){
        def mvnHome =  tool name: "Maven-Install", type: "maven"
        def mvnCMD = "${mvnHome}/bin/mvn"
        dir("item-catalog"){
          sh "pwd"
          sh "${mvnCMD} clean package"
        }
        dir("edge-service"){
          sh "pwd"
          sh "${mvnCMD} clean package"
        }
    }
  //  we are using odavid/jenkins-jnlp-slave image for jenking slave pod this pod will provide a docker runtime inside the pod
     stage("Docker Build Image"){
        dir("item-catalog"){
          sh "docker build -t item-catalog ."
        }
          dir("edge-service"){          
		  sh "docker build -t edge-service ."
        }
    }
    // kubernetes continious deploy plugin is required, this tell jenkins in which cluster to deploy the kubernetes manifest.
	// jenkins -> credentials -> global -> add credentials -> kind select (kubernetes configuration)-> put the content of entire kube config file there. 
	stage("Apply Kubernetes Manifest"){
	    dir("item-catalog"){
	        kubernetesDeploy(
	            configs : "deployment-service-item-catalog.yaml",
	            kubeconfigId : "kubeconfig"
	        )
        } 
        dir("edge-service"){
	        kubernetesDeploy(
	            configs : "deployment-service-edge-service.yaml",
	            kubeconfigId : "kubeconfig"
	        )
        } 
	  
	}
    
}