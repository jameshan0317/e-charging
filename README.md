## 전기자동차 충전관리 시스템 (e-charging)

![image](https://user-images.githubusercontent.com/61259324/122623241-4db1aa80-d0d6-11eb-9f65-4982d2072a33.png)

### Repositories

- **게이트웨이** - [https://github.com/jameshan0317/gateway.git](https://github.com/jameshan0317/gateway.git)

- **충전소관리** - [https://github.com/jameshan0317/echarger.git](https://github.com/jameshan0317/echarger.git)

- **충전 예약** - [https://github.com/jameshan0317/reservation.git](https://github.com/jameshan0317/reservation.git)

- **전기 충전** - [https://github.com/jameshan0317/echarging.git](https://github.com/jameshan0317/echarging.git)

- **My Page** - [https://github.com/jameshan0317/mypage.git](https://github.com/jameshan0317/mypage.git)



*전체 소스 받기*
```
git clone https://github.com/jameshan0317/e-charging.git
```

### Table of contents

- [서비스 시나리오](#서비스-시나리오)
  - [기능적 요구사항](#기능적-요구사항)
  - [비기능적 요구사항](#비기능적-요구사항)
- [분석/설계](#분석설계)
  - [Event Storming 결과](#event-storming-결과)
  - [헥사고날 아키텍처 다이어그램 도출](#헥사고날-아키텍처-다이어그램-도출)
- [구현](#구현)
  - [기능적 요구사항 검증](#기능적-요구사항-검증)
- [Saga](#saga)
- [CQRS](#cqrs)
- [Correlation](#correlation)
- [동기식 호출(Req/Resp)](#동기식-호출reqresp)
- [Gateway](#gateway)
- [Deploy / Pipeline](#deploy--pipeline)
- [Circuit Breaker와 Fallback 처리](#circuit-breaker와-fallback-처리)
- [Autoscale Out (HPA)](#autoscale-out-hpa)
- [Zero-Downtime deploy - Readiness Probe](#zero-downtime-deploy---readiness-probe)
- [ConfigMap](#configmap)
- [Polyglot Persistence](#polyglot-persistence)
- [Self-healing (Liveness Probe)](#self-healing-liveness-probe)


# 서비스 시나리오

## 기능적 요구사항

* 전기차 충전소 관리자는 전기충전기를 등록한다.
* 고객은 전기차 충전소를 필요한 시간대에 예약한다.
* 고객은 전기 충전 예약을 취소 할 수 있다.
* 전기차 충전 예약이 된 같은 시간대에는 충전 예약을 할 수 없다.
* 예약된 고객이 충전소에 방문하여 충전을 시작하면, 해당 충전기는 충전중 상태로 된다.
* 고객이 충전을 완료 하면, 충전 완료 상태가 된다.
* 고객은 예약 및 충전 상태 정보를 확인 할 수 있다.


## 비기능적 요구사항
* 트랜잭션
    * 전기차 충전 예약이 된 같은 시간대에는 충전 예약을 할 수 없다. (Sync 호출)
* 장애격리
    * 전기 충전 기능이 수행되지 않더라도 충전 예약은 365일 24시간 받을 수 있어야 한다. (Async (event-driven), Eventual Consistency)
    * 예약시스템이 과중 되면 사용자를 잠시동안 받지 않고 예약을 잠시후에 하도록 유도한다. (Circuit breaker, fallback)
* 성능
    * 고객은 MyPage에서 본인 예약 및 충전 상태를 확인 할 수 있어야 한다. (CQRS - 조회전용 서비스)
    
# 분석/설계
1. 이벤트를 식별하여 타임라인으로 배치, 부적격 이벤트 제거함
2. Event를 발생시키는 Command와 발생시키는주체(Actor)를 식별함
3. 연관있는도메인 이벤트들을 Aggregate로 묶음
4. 바운디드 컨텍스트로 묶음
5. 폴리시 부착/이동 및 바운디드 컨텍스트에 매핑함

## Event Storming 결과
![image](https://user-images.githubusercontent.com/61259324/123213654-3709ba00-d501-11eb-87e3-000211b209b8.png)

MSA-EZ 도구를 활용하여 이벤트 스토밍 및 설계/코드 Download

![image](https://user-images.githubusercontent.com/61259324/123215156-104c8300-d503-11eb-83b3-d109c0b6e470.png)

## 헥사고날 아키텍처 다이어그램 도출

![image](https://user-images.githubusercontent.com/61259324/123291299-f0da4800-d54c-11eb-8cda-decd70d14416.png)

# 구현
분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라,구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다
(각자의 포트넘버는 8081 ~ 8084, 8088 이다)
```shell
cd echarger
mvn spring-boot:run

cd reservation
mvn spring-boot:run 

cd echarging
mvn spring-boot:run 

cd mypage
mvn spring-boot:run

cd gateway
mvn spring-boot:run 
```


## 기능적 요구사항 검증
1. 전기차 충전소 관리자는 전기충전기를 등록한다.
   
![image](https://user-images.githubusercontent.com/61259324/123188195-956d7300-d4d6-11eb-9b98-636ed4deb007.png)

2. 고객은 전기차 충전소를 필요한 시간대에 예약한다.
   
![image](https://user-images.githubusercontent.com/61259324/123188522-217f9a80-d4d7-11eb-8dd2-17cbaaa48863.png)

3. 고객은 전기 충전 예약을 취소 할 수 있다.

![image](https://user-images.githubusercontent.com/61259324/123188641-568bed00-d4d7-11eb-95b1-af397ab7ccba.png)

4. 전기차 충전 예약이 된 같은 시간대에는 충전 예약을 할 수 없다.
  ( 해당시간대에 예약되어 있지 않으면 예약 가능)

![image](https://user-images.githubusercontent.com/61259324/123188725-7c18f680-d4d7-11eb-9ffa-463d289c4a55.png)

5. 예약된 고객이 충전소에 방문하여 충전을 시작하면, 해당 충전기는 충전중 상태로 된다.
   (충전중 상태 : CHARGING_STARTED)

![image](https://user-images.githubusercontent.com/61259324/123188807-a23e9680-d4d7-11eb-9f6e-5b78d3f60ef2.png)

6. 고객이 충전을 완료 하면, 충전 완료 상태가 된다.
   (충전완료 상태 : CHARGING_ENDED)

![image](https://user-images.githubusercontent.com/61259324/123188867-c8fccd00-d4d7-11eb-9f3c-cd78d549a637.png)



7. 고객은 예약 및 충전 상태 정보를 확인 할 수 있다. 

![image](https://user-images.githubusercontent.com/61259324/123188950-e6319b80-d4d7-11eb-865f-c1f861d9813c.png)



# Saga

분석/설계 및 구현을 통해 이벤트를 Publish/Subscribe 하도록 구현함

[Publish]

![image](https://user-images.githubusercontent.com/61259324/123189438-c9499800-d4d8-11eb-85ac-4e782d3cf531.png)

[Subscribe]

![image](https://user-images.githubusercontent.com/61259324/123189542-f1d19200-d4d8-11eb-827d-5bc7038a8647.png)




# CQRS
타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이)도 내 서비스의 충전 예약 신청 내역 조회가 가능하게 구현해 두었다. 
본 프로젝트에서 View 역할은 mypage 서비스가 수행함

[충전 예약 신청 후 mypage 조회]

![image](https://user-images.githubusercontent.com/61259324/123190832-3d853b00-d4db-11eb-96c8-79ee04985a21.png)


[충전 종료후 mypage 조회]

![image](https://user-images.githubusercontent.com/61259324/123190231-1f6b0b00-d4da-11eb-86cd-e551d3c2604b.png)




# Correlation
각 이벤트 건(메시지)이 어떤 Policy를 처리할 때 어떤건에 연결된 처리건인지를 구별하기 위한 Correlation-key를 제대로 연결하였는지를 검증함

![image](https://user-images.githubusercontent.com/61259324/123191227-e59b0400-d4db-11eb-9128-b910be029284.png)



# 동기식 호출(Req/Resp) 

전기차 충전예약(reservation) -> 충전소(echarger) 예약 가능 Check 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리했으며. 호출 프로토콜은 RestController를 FeignClient를 이용하여 호출하였음.


(Reservation) EchargerService.java
```java
package echarging.external;

@FeignClient(name="echarger", url="${api.url.echarger}", fallback = EchargerServiceFallback.class)
public interface EchargerService {

    @RequestMapping(method= RequestMethod.GET, path="/echargers/chkAndRsrvTime")  
    public boolean chkAndRsrvTime(@RequestParam Long chargerId);

}
```


충전 예약을 받은 직후 충전소 예약가능 확인을 요청하도록 처리
(Reservation) EchargerController.java
```java
package echarging;

@RestController
public class EchargerController {

    @Autowired
    EchargerRepository echargerRepository;

    @RequestMapping(value = "/echargers/chkAndRsrvTime",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8")

    public boolean chkAndRsrvTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("##### /echargers/chkAndRsrvTime  called #####");

        boolean status = false;

        Long echargerId = Long.valueOf(request.getParameter("chargerId"));

        Optional<Echarger> echarger = echargerRepository.findById(echargerId);
        if(echarger.isPresent()) {
            Echarger echargerValue = echarger.get();

            //Hystrix Timeout 점검
            if(echargerValue.getChargerId() == 2) {
                System.out.println("### Hystrix 테스트를 위한 강제 sleep 5초 ###");
                Thread.sleep(5000);
            }
            //예약 가능한지 체크
            if(echargerValue.getRsrvTimeAm() == null || echargerValue.getRsrvTimePm() == null) {
                status = true;

                //예약 가능하면 예약할 시간대 선택/저장
                if(echargerValue.getRsrvTimeAm() == null){
                        echargerValue.setRsrvTimeAm("Y");
                }else if(echargerValue.getRsrvTimePm() == null){
                        echargerValue.setRsrvTimePm("Y");
                }    

                echargerRepository.save(echargerValue);
            }
        }

        return status;
    }
}
```

동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 충전소 관리 시스템이 장애가 나면 충전 예약도 못 받는다는 것을 확인:

충전소 관리(echarger) 서비스를 잠시 내려놓음 (ctrl+c)

충전예약처리
```
http POST localhost:8088/reservations chargerId=1 rsrvTimeAm=Y userId=1   #Fail
http POST localhost:8088/reservations chargerId=2 rsrvTimePm=Y userId=1   #Fail
```
충전소 관리 서비스 재기동
```
cd echarger
mvn spring-boot:run
```
충전예약처리
```
http POST localhost:8088/reservations chargerId=1 rsrvTimeAm=Y userId=1   # Success
http POST localhost:8088/reservations chargerId=2 rsrvTimePm=Y userId=1   # Success
```

운영단계에서는 Circuit Breaker를 이용하여 충전소 관리 시스템에 장애가 발생하여도 충전예약 접수는 가능하도록 하였음

# Gateway
API GateWay를 통하여 마이크로 서비스들의 진입점을 통일할 수 있다. 다음과 같이 GateWay를 적용하여 모든 마이크로서비스들은 http://localhost:8088/{context}로 접근할 수 있음.

```yaml
server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: echarger
          uri: http://localhost:8081
          predicates:
            - Path=/echargers/**
        - id: reservation
          uri: http://localhost:8082
          predicates:
            - Path=/reservations/** 
        - id: echarging
          uri: http://localhost:8083
          predicates:
            - Path=/echargings/** 
        - id: mypage
          uri: http://localhost:8084
          predicates:
            - Path= /mypages/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: echarger
          uri: http://echarger:8080
          predicates:
            - Path=/echargers/** 
        - id: reservation
          uri: http://reservation:8080
          predicates:
            - Path=/reservations/** 
        - id: echarging
          uri: http://echarging:8080
          predicates:
            - Path=/echargings/** 
        - id: mypage
          uri: http://mypage:8080
          predicates:
            - Path= /mypages/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080

```
# Deploy / Pipeline

git에서 소스 가져오기
```
git clone https://github.com/jameshan0317/e-charging.git
```

Build 하기
```bash
cd /echarger
mvn package

cd /reservation
mvn package

cd /echarging
mvn package

cd /mypage
mvn package

cd /gateway
mvn package
```


Docker Image Build/Push, deploy/service 생성 (yml 이용)

namespace 생성
```
kubectl create ns e-charging
```

reservation 서비스는 무정지 재배포 테스트를 위해 v1, latest 두 개 버전 build,push
```
cd reservation
az acr build --registry jameshan055 --image jameshan055.azurecr.io/reservation:v1 .
az acr build --registry jameshan055 --image jameshan055.azurecr.io/reservation:latest .

cd kubernetes
kubectl apply -f deployment.yml -n e-charging
kubectl apply -f service.yaml -n e-charging
```
```
cd echarger
az acr build --registry jameshan055 --image jameshan055.azurecr.io/echarger:latest . 

cd kubernetes
kubectl apply -f deployment.yml -n e-charging
kubectl apply -f service.yaml -n e-charging
```
```
cd echarging
az acr build --registry jameshan055 --image jameshan055.azurecr.io/echarging:latest .

cd kubernetes
kubectl apply -f deployment.yml -n e-charging
kubectl apply -f service.yaml -n e-charging
```
```
cd mypage
az acr build --registry jameshan055 --image jameshan055.azurecr.io/mypage:latest . 

cd kubernetes
kubectl apply -f deployment.yml -n e-charging
kubectl apply -f service.yaml -n e-charging
```
```
cd gateway
az acr build --registry jameshan055 --image jameshan055.azurecr.io/gateway:latest . 

cd kubernetes
kubectl apply -f deployment.yml -n e-charging
kubectl apply -f service.yaml -n e-charging
```

yml 파일 이용한 deploy
(e-charging/reservation/kubernetes/deployment.yml 파일) 
```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: reservation
  namespace: e-charging
  labels:
    app: reservation
spec:
  replicas: 1
  selector:
    matchLabels:
      app: reservation
  template:
    metadata:
      labels:
        app: reservation
    spec:
      containers:
        - name: reservation
          image: jameshan055.azurecr.io/reservation:latest
          ports:
            - containerPort: 8080
          env:
            - name: echarger-url
              valueFrom:
                configMapKeyRef:
                  name: echargerurl
                  key: url
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
          resources:
            requests:
              memory: "64Mi"
              cpu: "250m"
            limits:
              memory: "500Mi"
              cpu: "500m" 

```


(e-charging/reservation/kubernetes/service.yml 파일)
```yml
apiVersion: v1
kind: Service
metadata:
  name: reservation
  namespace: e-charging
  labels:
    app: reservation
spec:
  ports:
    - port: 8080
      targetPort: 8080
  selector:
    app: reservation

```

deploy 완료

![image](https://user-images.githubusercontent.com/61259324/123202090-ed17d880-d4ee-11eb-8762-f3635451b80b.png)

# Circuit Breaker와 Fallback 처리
- Spring FeignClient + Hystrix를 사용하여 구현함

시나리오는 예약(Reservation)-->충전소(echarger) 확인 시 예약 요청에 대한 충전소 예약가능시간대 확인이 3초를 넘어설 경우 Circuit Breaker 를 통하여 장애 격리

Hystrix 를 설정: FeignClient 요청처리에서 처리시간이 3초가 넘어서면 CB가 동작하도록 (요청을 빠르게 실패처리, 차단) 설정 추가로, 테스트를 위해 1번만 timeout이 발생해도 CB가 발생하도록 설정

(application.yml)

![image](https://user-images.githubusercontent.com/61259324/123202509-b2627000-d4ef-11eb-94c8-d88f5ee3f174.png)



호출 서비스(예약)에서는 충전소API 호출에서 문제 발생 시 예약건을 Out of available Time 처리하도록 FallBack 구현함

(Reservation) EchargerService.java
```java
package echarging.external;
 ...
@FeignClient(name="echarger", url="${api.url.echarger}", fallback = EchargerServiceFallback.class)
public interface EchargerService {

    @RequestMapping(method= RequestMethod.GET, path="/echargers/chkAndRsrvTime")  
    public boolean chkAndRsrvTime(@RequestParam Long chargerId);

}
```

(Reservation) EchargerServiceFallBack.java
```java
package echarging.external;
  ...
@Component
public class EchargerServiceFallback implements EchargerService {
    @Override
    public boolean chkAndRsrvTime(@RequestParam Long chargerId) {
        System.out.println("Circuit breaker has been opened. Fallback returned instead.");
        return false;
    }  
}
```
(Reservation) Reservation.java

![image](https://user-images.githubusercontent.com/61259324/123203049-b347d180-d4f0-11eb-8f6f-fd4692797c64.png)


피호출 서비스(충전소 : echarger )에서 테스트를 위해 chargerId가 2인 예약건에 대해 sleep 처리

(Echarger) EchargerController.java

![image](https://user-images.githubusercontent.com/61259324/123203202-f144f580-d4f0-11eb-8915-f5db15dd2439.png)


서킷 브레이커 동작 확인:
chargerId가 1번 인 경우 정상적으로 주문 처리 완료
```
http POST http://52.231.156.9:8080/reservations chargerId=1 rsrvTimeAm=Y userId=3993
```
![image](https://user-images.githubusercontent.com/61259324/123203402-4254e980-d4f1-11eb-8d53-d4566fcbe513.png)

chargerId가 2번 인 경우 CB에 의한 timeout 발생 확인 (예약건은 Out of available Time 처리됨)
![image](https://user-images.githubusercontent.com/61259324/123203467-657f9900-d4f1-11eb-8651-993fd954ea0e.png)

일정시간 뒤에는 다시 주문이 정상적으로 수행되는 것을 알 수 있다.
![image](https://user-images.githubusercontent.com/61259324/123203554-83e59480-d4f1-11eb-9d97-1df67ec08bfc.png)

운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 Thread 자원 등을 보호하고 있음을 증명함

# Autoscale Out (HPA)

충전소 서비스가 몰릴 경우를 대비하여 자동화된 확장 기능을 적용하였음.

충전소 서비스에 리소스에 대한 사용량을 정의함 
(echarger/kubernetes/deployment.yml)

```yml
    resources:
            requests:
              memory: "64Mi"
              cpu: "250m"
            limits:
              memory: "500Mi"
              cpu: "500m"
```

충전소 서비스에 대한 replica를 동적으로 늘려주도록 HPA를 설정한다. 설정은 CPU 사용량이 15%를 넘어서면 replica를 3개까지 늘려줌
```
kubectl autoscale deploy echarger --min=1 --max=3 --cpu-percent=15 -n e-charging
```

워크로드를 100명이 2분 동안 걸어줌
```
kubectl exec -it pod/siege -c siege -n e-charging -- /bin/bash

# siege -c100 -t120S -r10 -v --content-type "application/json" 'http://52.231.156.9:8080/echargers POST {"cgName": "이마트충전소"}'
```

오토스케일 확인을 위해 모니터링을 걸어둔다.
```
watch kubectl get all -n e-charging
```

잠시 후 echarger에 대해 스케일 아웃이 발생하는 것을 확인할 수 있음
![image](https://user-images.githubusercontent.com/61259324/123204422-0e7ac380-d4f3-11eb-8f5a-af8e1ec296b3.png)


# Zero-Downtime deploy - Readiness Probe
deployment.yml에 정상 적용되어 있는 readinessProbe

![image](https://user-images.githubusercontent.com/61259324/123204754-ae385180-d4f3-11eb-838e-02a66800ea28.png)

readiness 설정 제거한 yml 파일로 echarger deploy 다시 생성 후, siege 부하 테스트 실행해둔 뒤 재배포를 진행함

siege 테스트
``` 
kubectl exec -it pod/siege -c siege -n e-charging -- /bin/bash

# siege -c100 -t120S -r10 -v --content-type "application/json" 'http://52.231.156.9:8080/echargers POST {"cgName": "이마트충전소"}'
```
echarger 새버전으로의 배포 시작 (두 개 버전으로 버전 바꿔가면서 테스트)
```
(Readiness 설정을 뺀 파일)
kubectl apply -f deployment_test_readiness.yml -n e-charging
(Readiness 설정 파일) 
kubectl apply -f deployment.yml -n e-charging
```
새 버전으로 배포되는 중 (구버전, 신버전 공존)
![image](https://user-images.githubusercontent.com/61259324/123279598-f5016800-d542-11eb-962b-80e80c8ccdea.png)

![image](https://user-images.githubusercontent.com/61259324/123279428-cedbc800-d542-11eb-98d2-8ecfd918c3b3.png)


배포기간중 Availability 가 100%가 안 되는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기 위해 Readiness Probe 를 설정함

![image](https://user-images.githubusercontent.com/61259324/123280435-ba4bff80-d543-11eb-9c40-6d4952b11a0b.png)

![image](https://user-images.githubusercontent.com/61259324/123280873-23337780-d544-11eb-8bf0-e4fe18a6024f.png)

다시 readiness 정상 적용 후(deployment.yml), Availability 100% 확인

![image](https://user-images.githubusercontent.com/61259324/123281179-6beb3080-d544-11eb-8283-ab221a96a77d.png)


# ConfigMap
시스템별로 변경 가능성이 있는 설정들을 ConfigMap을 사용하여 관리

e-charging시스템에서는 충전예약 서비스에서 충전소 서비스의 예약가능 check 호출 시 "호출 주소"를 ConfigMap 처리하였음.

Java 소스에 "호출 주소"를 변수(api.url.echarger)처리함
(/reservation/src/main/java/echarging/external/EchargerService.java)
```java
package echarging.external;
 ...
@FeignClient(name="echarger", url="${api.url.echarger}", fallback = EchargerServiceFallback.class)
public interface EchargerService {

    @RequestMapping(method= RequestMethod.GET, path="/echargers/chkAndRsrvTime")  
    public boolean chkAndRsrvTime(@RequestParam Long chargerId);
}
```
application.yml 파일에서 api.url.echarger를 ConfigMap과 연결

reservation application.yml (reservation/src/main/resources/application.yml)
![image](https://user-images.githubusercontent.com/61259324/123207057-bdb99980-d4f7-11eb-84a0-226516d21fb2.png)

reservation deployment.yml 에 적용 (reservation/kubernetes/deployment.yml)
![image](https://user-images.githubusercontent.com/61259324/123207180-f194bf00-d4f7-11eb-839b-f4ce84bf81b0.png)

ConfigMap 생성 후 조회
```
kubectl create configmap echargerurl --from-literal=url=http://echarger:8080 -n e-charging
kubectl get configmap echargerurl -o yaml -n e-charging
```
![image](https://user-images.githubusercontent.com/61259324/123207323-2dc81f80-d4f8-11eb-8eaf-67d393e1aa49.png)

reservation pod 내부로 들어가서 환경변수도 확인
```
kubectl exec -it pod/reservation-669bf984fc-88qxq -n e-charging -- /bin/sh
# env
```
![image](https://user-images.githubusercontent.com/61259324/123207505-72ec5180-d4f8-11eb-9234-f653860ac58b.png)

# Polyglot Persistence
mypage 서비스의 DB와 echarger/reservation/echarging 서비스의 DB를 다른 DB를 사용하여 MSA간 서로 다른 종류의 DB간에도 문제 없이 동작하여 다형성을 만족하는지 확인하였다.
(Polyglot을 만족)

|서비스|DB|pom.xml|
| :--: | :--: | :--: |
|echarger| H2 |![image](https://user-images.githubusercontent.com/2360083/121104579-4f10e680-c83d-11eb-8cf3-002c3d7ff8dc.png)|
|reservation| H2 |![image](https://user-images.githubusercontent.com/2360083/121104579-4f10e680-c83d-11eb-8cf3-002c3d7ff8dc.png)|
|echarging| H2 |![image](https://user-images.githubusercontent.com/2360083/121104579-4f10e680-c83d-11eb-8cf3-002c3d7ff8dc.png)|
|mypage| HSQL |![image](https://user-images.githubusercontent.com/2360083/120982836-1842be00-c7b4-11eb-91de-ab01170133fd.png)|


# Self-healing (Liveness Probe)

deployment.yml에 정상 적용되어 있는 livenessProbe

![image](https://user-images.githubusercontent.com/61259324/123209291-5b629800-d4fb-11eb-811d-fdb738155003.png)

Self-healing 확인을 위한 Liveness Probe 옵션 변경 (Port 변경)
설정해놓은 deploy로 다시 배포 후, retry 시도 확인 (echarger서비스)

deployment_Test_liveness.yml

![image](https://user-images.githubusercontent.com/61259324/123209620-d9bf3a00-d4fb-11eb-88d6-1554e1461fa6.png)


Liveness 확인 실패에 따른 retry발생 확인

![image](https://user-images.githubusercontent.com/61259324/123285328-e8cbd980-d547-11eb-82ee-7507b9b6f55f.png)

이상으로 12가지 체크포인트가 구현 및 검증 완료되었음 확인함.
