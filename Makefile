.PHONY: build test run-dev clean docker-build docker-run

build:
	mvn clean package -DskipTests

test:
	mvn clean test

run-dev:
	mvn spring-boot:run -Dspring-boot.run.profiles=dev

clean:
	mvn clean

docker-build:
	docker build -t pms-crossref .

docker-run:
	docker-compose -f docker-compose.dev.yml up

docker-stop:
	docker-compose -f docker-compose.dev.yml down

help:
	@echo "Available targets:"
	@echo "  build      - Build the application"
	@echo "  test       - Run tests"
	@echo "  run-dev    - Run in development mode"
	@echo "  clean      - Clean build artifacts"
	@echo "  docker-build - Build Docker image"
	@echo "  docker-run - Start full stack with Docker Compose"
	@echo "  docker-stop - Stop Docker Compose stack"