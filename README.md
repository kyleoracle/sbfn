
# sbfn

this demo uses oracle function to listen object create event to download and parse excel and put data into ADW

- official document

        https://docs.cloud.oracle.com/iaas/Content/Functions/Concepts/functionsoverview.htm

- account

        tenancy name = xxx

        https://console.us-ashburn-1.oraclecloud.com/a/compute/instances?_adf.ctrl-state=undefined&_afrLoop=undefined
        kyle/xxx

- create compartment e.g. xxx_COMPARTMENT
- create ADW instance e.g. xxxAWD

        - ADMIN/xxx
        - download wallet
        - create user

                create user xxx identified by "xxx";
                grant dwrole to xxx;
                ALTER USER xxx quota unlimited on DATA;

- setup fn with the hello world demo

        - complete guide

                https://www.oracle.com/webfolder/technetwork/tutorials/infographics/oci_faas_gettingstarted_quickview/functions_quickview_top/functions_quickview/index.html#

        - tenancy level policies

                Allow group fngroup to manage repos in tenancy
                Allow service FaaS to read repos in tenancy
                allow service cloudEvents to use functions-family in tenancy (note: this is for listening object storage event)

        - compartment level policies

                Allow group fngroup to manage functions-family in compartment xxx_COMPARTMENT
                Allow group fngroup to manage vnics in compartment xxx_COMPARTMENT
                Allow group fngroup to inspect subnets in compartment xxx_COMPARTMENT
                Allow service FaaS to use virtual-network-family in compartment xxx_COMPARTMENT
                allow group fngroup to manage cloudevents-rules in tenancy (note: this is for listening object storage event)

        - create vm

                IP is x.x.x.x

                sudo -i
                yum install -y docker-engine
                systemctl enable docker
                systemctl start docker
                mkdir ~/.oci
                openssl genrsa -out ~/.oci/fn_private.pem -aes128 2048       (xxx as password)
                chmod go-rwx ~/.oci/fn_private.pem
                openssl rsa -pubout -in ~/.oci/fn_private.pem -out ~/.oci/fn_pub.pem

                curl -LSs https://raw.githubusercontent.com/fnproject/cli/master/install | sh
                cd /usr/bin
                ln -s /usr/local/bin/fn fn

                fn create context mycontext --provider oracle
                fn use context mycontext
                fn update context oracle.compartment-id ocid1.compartment.oc1..xxx
                fn update context api-url https://functions.us-ashburn-1.oraclecloud.com
                fn update context registry iad.ocir.io/xxx/myrepo
                fn update context oracle.profile fn

                docker login iad.ocir.io
                xxx/kyle
                xxx

                cd /home/opc
                fn use context mycontext
                fn init --runtime java helloworld-func
                cd helloworld-func
                create helloworld-app in function ui
                fn deploy --app helloworld-app (increase image version) / fn --verbose deploy --app helloworld-app (show build log)
                fn invoke helloworld-app helloworld-func

        - (optional) start a local fn server

                cd /home/opc
                fn start > out.log 2>&1 &
                fn use context default
                fn create app helloworld-app
                cd helloworld-func
                fn --verbose deploy --app helloworld-app --local    (kernel memory accounting disabled in this runc bu)
                fn invoke helloworld-app helloworld-func
                echo kk | fn invoke helloworld-app helloworld-func (echo params into function)
                curl http://localhost:8080/t/helloworld-app/helloworld-func  (init requires --trigger http)

- configure the sbfn project

        - extract wallet files into sbfn/wallet
        - copy files in ~/.oci to sbfn/oci

- deploy this project

        cd /home/opc/sbfn

        fn create app --annotation oracle.com/oci/subnetIds='["ocid1.subnet.oc1.iad.xxx"]' --config DB_PASSWORD=xxx --config DB_SERVICE_NAME=xxx_medium --config DB_USER=xxx sbfn

        ./deploy.sh

- create object storage bucket e.g. SR081400_BUCKET, enable Emit Object Events
- configure event rule following official guide

        - configure it to listen object create event and trigger the sbfn
        - configure its attribute with bucket name = SR081400_BUCKET (does not work)

- test

        - upload excel file to bucket

                https://console.us-ashburn-1.oraclecloud.com/object-storage/buckets/xxx/SR081400-BUCKET/objects

        - refresh event rule matrix

                https://console.us-ashburn-1.oraclecloud.com/events/rules/ocid1.eventrule.oc1.iad.xxx/metrics

        - refresh fn matric

                https://console.us-ashburn-1.oraclecloud.com/functions/apps/ocid1.fnapp.oc1.iad.xxx/metrics

        - refresh fn log

                https://console.us-ashburn-1.oraclecloud.com/object-storage/buckets/idyij23nuvbf/oci-logs._functions.ocid1.compartment.oc1..xxx/objects

        - refresh ADW tables in sqldeveloper to check if there is any new table is created

- if any of these issues

        - could not resolve the connect identifier  "xxx_medium" -> wrong wallet location
        - no suitable driver -> ucp is not included in docker build
        - korean language cannot be used as field, statment will misss some of them
