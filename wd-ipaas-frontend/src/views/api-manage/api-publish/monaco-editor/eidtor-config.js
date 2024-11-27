export default {
  language: {
    apex: 'apex',
    azcli: 'azcli',
    bat: 'bat',
    c: 'c',
    clojure: 'clojure',
    coffeescript: 'coffeescript',
    cpp: 'cpp',
    csharp: 'csharp',
    csp: 'csp',
    css: 'css',
    dockerfile: 'dockerfile',
    fsharp: 'fsharp',
    go: 'go',
    graphql: 'graphql',
    handlebars: 'handlebars',
    html: 'html',
    ini: 'ini',
    java: 'java',
    javascript: 'javascript',
    json: 'json',
    kotlin: 'kotlin',
    less: 'less',
    lua: 'lua',
    markdown: 'markdown',
    msdax: 'msdax',
    mysql: 'mysql',
    'objective-c': 'objective-c',
    pascal: 'pascal',
    perl: 'perl',
    pgsql: 'pgsql',
    php: 'php',
    plaintext: 'plaintext',
    postiats: 'postiats',
    powerquery: 'powerquery',
    powershell: 'powershell',
    pug: 'pug',
    python: 'python',
    r: 'r',
    razor: 'razor',
    redis: 'redis',
    redshift: 'redshift',
    ruby: 'ruby',
    rust: 'rust',
    sb: 'sb',
    scheme: 'scheme',
    scss: 'scss',
    shell: 'shell',
    sol: 'sol',
    sql: 'sql',
    st: 'st',
    swift: 'swift',
    tcl: 'tcl',
    typescript: 'typescript',
    vb: 'vb',
    xml: 'xml',
    yam: 'yam'
  },
  theme: {
    vs: 'vs',
    'vs-dark': 'vs-dark',
    'hc-black': 'hc-black'
  },
  codes: {
    javascript: `function initializeProperties(target, members) {
  var keys = Object.keys(members);
  var properties;
  var i, len;
  for (i = 0, len = keys.length; i < len; i++) {
    var key = keys[i];
    var enumerable = key.charCodeAt(0) !== /*_*/95;
    var member = members[key];
    if (member && typeof member === 'object') {
      if (member.value !== undefined || typeof member.get === 'function' || typeof member.set === 'function') {
        if (member.enumerable === undefined) {
          member.enumerable = enumerable;
        }
        properties = properties || {};
        properties[key] = member;
        continue;
      }
    }
    if (!enumerable) {
      properties = properties || {};
      properties[key] = { value: member, enumerable: enumerable, configurable: true, writable: true }
      continue;
    }
    target[key] = member;
  }
  if (properties) {
    Object.defineProperties(target, properties);
  }
}`,
    python: `import banana

class Monkey:
    # Bananas the monkey can eat.
    capacity = 10
    def eat(self, n):
        """Make the monkey eat n bananas!"""
        self.capacity -= n * banana.size

    def feeding_frenzy(self):
        self.eat(9.25)
        return "Yum yum"
`,
    shell: `# Link filedescriptor 10 with stdin
exec 10<&0
# stdin replaced with a file supplied as a first argument
exec < $1
# remember the name of the input file
in=$1

# init
file="current_line.txt"
let count=0

# this while loop iterates over all lines of the file
while read LINE
do
    # increase line counter
    ((count++))
    # write current line to a tmp file with name $file (not needed for counting)
    echo $LINE > $file
    # this checks the return code of echo (not needed for writing; just for demo)
    if [ $? -ne 0 ]
     then echo "Error in writing to file; check its permissions!"
    fi
done`
  }
};
