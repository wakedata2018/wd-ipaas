function getDomain() {
  let result = null;
  const host = window.location.hostname;
  const ipPatt = new RegExp(`^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$`);
  if (ipPatt.test(host) === true) {
    result = {
      type: 'ip',
      domain: host
    };
  } else if (host === 'localhost') {
    result = {
      type: 'localhost',
      domain: 'localhost'
    };
  } else if (host.indexOf('.') !== -1) {
    const domains = host.split('.');
    let hrefName = domains[0];
    const domainPatt = new RegExp(`^${hrefName}\\.`);
    let domain = host.replace(domainPatt, '.');
    result = {
      type: 'domain',
      domain: domain
    };
  } else {
    result = {
      type: 'unknown',
      domain: host
    };
  }
  return result;
}
export default {getDomain};