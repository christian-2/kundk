#!/usr/bin/python3

from datetime import datetime

import argparse
import jwt
import sys

parser = argparse.ArgumentParser()
parser.add_argument("--blob", default="/mnt/blob.jwt")
parser.add_argument("--aaguid")
args = parser.parse_args()

with open(args.blob, "rb") as fh:
  encoded_jwt = fh.read()
payload = jwt.decode(encoded_jwt, options={"verify_signature": False})

if datetime.now() >= datetime.strptime(payload["nextUpdate"], "%Y-%m-%d"):
  sys.exit(f"update necessary: {args.blob}")

if args.aaguid:
  if args.aaguid == "00000000-0000-0000-0000-000000000000":
    description = args.aaguid
  else:
    description = next((entry["metadataStatement"]["description"]
      for entry in payload["entries"] 
      if "aaguid" in entry and entry["aaguid"] == args.aaguid), args.aaguid)
  print(description)
