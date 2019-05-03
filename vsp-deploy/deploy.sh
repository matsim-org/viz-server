#!/bin/bash
# VSP deploy script for travis CI
DOMAIN=$1
echo "domain is: $DOMAIN"
# check whether ssh-agent is present
eval $(ssh-agent -s)
# see bug with line endings at https://gitlab.com/gitlab-examples/ssh-private-key/issues/1
# this works but needs a base64 encoded key with cmd "cat id_rsa | base64 -w0"
# paste the output of cmd into var VSP_SSH_KEY_BASE64 in double-quotes (var in Travis)
ssh-add <(echo "$VSP_SSHKEY_VIZDEPLOY_CNODE00_BASE64"| base64 --decode)
# remove old webfiles
ssh vizdeploy@cnode00.vsp.tu-berlin.de "rm -r /srv/www/viz-auth$DOMAIN/app/*"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "rm -r /srv/www/viz-files$DOMAIN/app/*"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "rm -r /srv/www/viz-frani$DOMAIN/app/*"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "rm -r /srv/www/viz-pprocemis$DOMAIN/app/*"
# upload fresh built files
scp -r auth/target/auth-1.0-SNAPSHOT.jar vizdeploy@cnode00.vsp.tu-berlin.de:/srv/www/viz-auth$DOMAIN/app/
scp -r files/target/files-1.0-SNAPSHOT.jar vizdeploy@cnode00.vsp.tu-berlin.de:/srv/www/viz-files$DOMAIN/app/
scp -r frame-animation/target/frame-animation-1.0-SNAPSHOT.jar vizdeploy@cnode00.vsp.tu-berlin.de:/srv/www/viz-frani$DOMAIN/app/
scp -r postprocessing-emissions/target/postprocessing-emissions-1.0-SNAPSHOT.jar vizdeploy@cnode00.vsp.tu-berlin.de:/srv/www/viz-pprocemis$DOMAIN/app/
# set correct permissions
ssh vizdeploy@cnode00.vsp.tu-berlin.de "chmod 0664 /srv/www/viz-auth$DOMAIN/app/auth-1.0-SNAPSHOT.jar"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "chmod 0664 /srv/www/viz-files$DOMAIN/app/files-1.0-SNAPSHOT.jar"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "chmod 0664 /srv/www/viz-frani$DOMAIN/app/frame-animation-1.0-SNAPSHOT.jar"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "chmod 0664 /srv/www/viz-pprocemis$DOMAIN/app/postprocessing-emissions-1.0-SNAPSHOT.jar"
# build new docker image 
ssh vizdeploy@cnode00.vsp.tu-berlin.de "sudo -u dockerbuilder /home/vizdeploy/bin/build_viz-auth$DOMAIN.sh"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "sudo -u dockerbuilder /home/vizdeploy/bin/build_viz-files$DOMAIN.sh"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "sudo -u dockerbuilder /home/vizdeploy/bin/build_viz-frani$DOMAIN.sh"
ssh vizdeploy@cnode00.vsp.tu-berlin.de "sudo -u dockerbuilder /home/vizdeploy/bin/build_viz-pprocemis$DOMAIN.sh"
