# Inventory Management API
Projeto backend utilizando __Spring Boot__, para a movimentação de estoque.

## Instruções
A API já está UP no [Heroku](https://diogob-inventory-management.herokuapp.com/actuator/health).  
Para testar a API utilizar a [Collection do Postman](https://github.com/dboegershausen/inventory-management/blob/master/Inventory-Management.postman_collection.json) localizada na raiz do projeto.  
__Obs 1:__ Executar o endpoint de autenticação antes de utilizar as demais features.  
__Obs 2:__ O token de autenticação tem validade de apenas 5 minutos.

## Features
- Cadastro de Produtos
- Movimentação de Estoque

## Tecnologias
- Spring
- DB PostgreSQL
- Liquibase
- Autenticação com JWT
- Testes Unitários
- Pipeline com Github, CircleCI, SonarCloud, Heroku

## Pipeline
- CircleCI : https://app.circleci.com/pipelines/github/dboegershausen/inventory-management
- SonarCloud : https://sonarcloud.io/summary/overall?id=dboegershausen_inventory-management
- Heroku : https://diogob-inventory-management.herokuapp.com/actuator/health



