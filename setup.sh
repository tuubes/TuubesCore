#!/bin/bash
set -e # Makes the script stop on error.
cd "$(dirname "$0")"

# Download the coloration script if needed
if [ ! -f 'terminal-colors.sh' ]; then
	wget -q --show-progress 'https://raw.githubusercontent.com/RPM-Outpost/common-utils/master/scripts/terminal-colors.sh'
fi
# Use it:
source terminal-colors.sh

# Setup the project
disp "${bold}Downloading the repositories...$reset"
base_url='https://github.com/mcphoton'
repos=('Photon-Server' 'Photon-ProtocolLib' 'Utils')
for repo in ${repos[@]}; do
	if [ ! -d $repo ]; then
		git clone "$base_url/$repo.git"
		disp "$green$repo downloaded."
	else
		disp "$green$repo already exists."
	fi
	style $reset
done
echo '----------'

# Create fetch-all.sh and pull-all.sh
disp "${bold}Creating the update scripts...$reset"
write_script() {
	script="$1"
	cmd="$2"
	msg="$3"
	cat > $script << END
#!/bin/bash
cd "\$(dirname "\$0")"
for dir in */; do
	echo -e "${bold}${msg}ing \$dir$reset"
	cd \$dir
	$cmd
	cd ..
done
echo '----------'
echo -e "${bgreen}${msg} completed."
END
	chmod a+x $script
}

write_script 'fetch-all.sh' 'git fetch --all' 'Fetch'
write_script 'pull-all.sh' 'git pull' 'Pull'
disp "Done!"
echo "----------"
disp "${bgreen}Setup completed."

